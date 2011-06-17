; SMT-Interface, version 20100812
; Matteo Pradella
; --------------------------------------------------------------------------
;
; Copyright (C) 2010 Matteo Pradella (pradella@elet.polimi.it)
;
; This program is free software; you can redistribute it and/or modify
; it under the terms of the GNU General Public License as published by
; the Free Software Foundation; either version 2 of the License, or
; (at your option) any later version.
;
; This program is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
; GNU General Public License for more details.
;
; You should have received a copy of the GNU General Public License
; along with this program; if not, write to the Free Software
; Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
;
; -------------------------------------------------------------------------

(in-package :cl-user)

(defpackage :smt-interface
  (:use :common-lisp
	:kripke
	:trio-utils
	)
  (:export :to-smt-and-back )) 


(in-package :smt-interface)

(defun to-smt-and-back (the-kripke smt-solver)
  "calls the SMT solver and gets its output"

  (format t "~%no CNF~% ") 
  (format t " 0.000 seconds of real time~%") ;; just for testing...
  (format t "~%2. SMT solving: ")

  (when (probe-file "output.1.txt")
    (delete-file "output.1.txt"))

  #+sbcl (time 
	  (ecase smt-solver
	    (:z3 
	     (format t "z3...~% ")(force-output)
	     (sb-ext:run-program "z3"
				 '("-smt" "-st" "-m" "output.smt.txt") :input
				 t :output "output.1.txt" :error t :search t :if-output-exists :supersede))

	    (:yices 
	     (format t "yices...~% ")(force-output)
	     (sb-ext:run-program "yices"
				 '("-m" "output.smt.txt") :input
				 t :output "output.1.txt" :error t :search t :if-output-exists :supersede))
	    (:cvc3
	     (format t "cvc3...~% ")(force-output)
	     (time (sb-ext:run-program "cvc3"
				       '("-lang" "smtlib" "-stats" "+model" "output.smt.txt" ) :input
				       t :output "output.1.txt" :error t :search t :if-output-exists :supersede)))
	    (:mathsat
	     (format t "mathsat... ~% ")(force-output)
	     (time (sb-ext:run-program "mathsat"
				       '("-input=smt" "-solve" "-print_model" "-logic=QF_IDL" "output.smt.txt") :input
				       t :output "output.1.txt" :error t :search t :if-output-exists :supersede)))))


  (let ((call-shell #+clisp   #'ext:shell
		    #+ecl     #'ext:system
		    #+gcl     #'system
		    #+abcl    #'ext:run-shell-command
		    #+clozure #'ccl::os-command))

    #+(or :sbcl :cmu) (declare (ignore call-shell))
    #-(or :sbcl :cmu) (time 
		       (ecase smt-solver
			 (:z3 
			  (format t "z3...~% ")(force-output)
			  (funcall call-shell "z3 -smt -st -m output.smt.txt > output.1.txt"))

			 (:yices 
			  (format t "yices...~% ")(force-output)
			  (funcall call-shell "yices -m output.smt.txt > output.1.txt"))

			 (:cvc3
			  (format t "cvc3...~% ")(force-output)
			  (funcall call-shell "cvc3 -lang smtlib -stats output.smt.txt +model > output.1.txt"))

			 (:mathsat
			  (format t "mathsat... ~% ")(force-output)
			  (funcall call-shell "mathsat -input=smt -solve -print_model -logic=QF_UFIDL output.smt.txt > output.1.txt"))))


    (unless (probe-file "output.1.txt")
      (error "Error: the SMT-solver is not installed"))

    ;; --- dump the output of the SMT ---
    #-(or :sbcl :clisp) (funcall call-shell "cat output.1.txt"))


  ;; --- parse the output of the SMT ---
  (let ((val (with-open-file (ff "output.1.txt" :direction :input)
	       (not (eq 'unsat (read ff))))))
    (format t (if val "---SAT---~%" "---UNSAT---~%"))
    (force-output)
    (when (and val (eq smt-solver :z3))
      (translate-smt-output (kripke-k the-kripke)))
    val)
  )


(defun translate-smt-output (k)
  (let ((dict (make-hash-table :test #'equal))
	(time (make-array (1+ k) :initial-element nil))
	(iht  (make-hash-table :test #'equal))
	(unk nil))

    (maphash (lambda (key val) (declare (ignore val)) ;; hash table for translating items
		     (setf (gethash key iht) 0)) *items*)
  
;just to see *items*  
;	(maphash (lambda (key val) (format t "item: ~s, ~s~%" key val)) *items*)

    (with-open-file (ff "output.1.txt" :direction :input)
      (loop 
	 with def  = (read ff)
	 with el = (cadr def)                 ;get element and its signature
	 with value   = (caddr def)           ;get its definition
	 with test = nil
	 with untimed = nil
	 with tme = 0
	 ;; unless (eq y '{)
	 ;; do 
	 ;;   (setf (gethash x dict) y)
	 ;; when (eq y '{)
	 ;; do	    	    
 	 ;;   (loop      
	 ;;      with tme = nil
	 ;;      with lst = '()
	 ;;      with ls  = nil
	    do
	    (if (not (consp el))
		  (setf el (list el)))

	    (setf test (not (member  (string-trim '(#\0 #\1 #\2 #\3 #\4 #\5 #\6 #\7 #\8 #\9) 
					   (string (car el))) '("P" "A" "I_EVE_P") :test #'equal)))

	    (setf untimed (eq 'uf (car (gethash (car el) *arith-items*))))      ;recognize untimed symbols

	    (if (or (eq (car el) 'loopex) 
		      (eq (car el) 'i_loop) 
		      untimed)
		  (setf (gethash (car el) dict) value))      ;if single element or atemporal items
	    	
		 

	    when test
	    do
	    (cond 
		  ;; ((numberp value) 
		  ;; 	(setf (aref time tme) (cons (car el) (aref time tme))))
		  ((and (consp value) (not untimed))
			(labels
			      ((rec-read-val (fm)
				     (let (   (par-lst (if (eq 'and (car (cadr fm)))
							           (mapcan #'last (cdr (cadr fm)))
							           (last (cadr fm))))                                ;value of all parameters (time included)
						(ls (caddr fm)) 
						(next-lst (cadddr fm)))
					   
					   
				                                                                    ;define the temporal instant
					   (if (> (length par-lst) 1)
						 (setf tme (car (last par-lst)))   ;in the case of list of parameters (and () () ())
						 (setf tme (car par-lst)))          ;in the case of one single parameter (the time)
					   (if (<= 0 tme k)
						 (if (numberp ls) 
						       (setf (aref time tme) (cons (list (car el) (butlast par-lst) ls) (aref time tme)))
						       (if (eq ls 'true)
							     (setf (aref time tme) (cons (list (car el) (butlast par-lst)) (aref time tme)))))
						)
					   (if (consp next-lst) 
						 (rec-read-val next-lst)))))
				    (rec-read-val value))))
  
	    do (setf def  (read ff))
	    (when (consp def)
	      (setf el (cadr def))                
	      (setf value (caddr def)))

	    (when (and (symbolp def) (eq 'sat def)) (return dict))
	    (when (and (symbolp def) (eq 'unknown def)) (setf unk t) (return dict))))

    ;DATA STRUCTURE
    ;time[i] = list of (object (list of parameters) (list of definitions - with IF ))

    ;; set loop variables    
    (let* ((gh (gethash 'i_loop dict))
	   (tt (when (and gh (<= 0 gh k)) 
		 (aref time gh))))
      (when (and gh (<= 0 gh k))
	(setf (aref time gh) (push  '(**LOOP**) tt)))) 
    (let* ((gh (gethash 'i_pool dict))
	   (tt (when (and gh (<= 0 gh k)) 
		 (aref time gh))))
      (when (and gh (<= 0 gh k))
	(setf (aref time gh) (push '(**POOL**) tt))))

    (with-open-file (ff "output.hist.txt" 
			:direction :output 
			:if-exists :supersede 
			:if-does-not-exist :create)

      ;; translate encoded items/arrays and dump the history
      (loop
       initially (maphash #'(lambda (key val) 
			      ;; (when (not (member (string-trim '(#\0 #\1 #\2 #\3 #\4 #\5 #\6 #\7 #\8 #\9) 
			      ;; 			       (string key)) '("P" "A" "I_EVE_P") :test #'equal))

				  (labels                                                           ;this for reading the atemporal items put in dict
					((rec-read-val (fm)
					       (if (consp fm)						     
						     (let (   (par-lst (if (eq 'and (car (cadr fm)))
									     (mapcan #'last (cdr (cadr fm)))
									     (last (cadr fm))))                                ;value of all parameters (time included)
								(v (caddr fm)) 
								(next-lst (cadddr fm)))
							   
							   (if (null par-lst)
								 (progn
								       (format t "~s = ~s~%" key v)
								       (format ff "~s = ~s~%" key v))
								 (progn
								       (format t "~s~s = ~s~%" key par-lst v)			          
								       (format ff "~s~s = ~s~%" key par-lst v))
								 )
							   (if (consp next-lst) 
								 (rec-read-val next-lst)))
						     (progn
							   (format t "~s = ~s~%" key fm)
							   (format ff "~s = ~s~%" key fm)
							   ))))
					      (rec-read-val val))) dict)
	 for i from 0 to k
	 do 
	   (format t  "------ time ~s ------~%" i)
	   (format ff "------ time ~s ------~%" i)
	   (loop 
	      with name = nil
	     for val in (aref time i)
	     ;manage non-symbol items

;	     unless (symbolp val)    ------- old version
	      do	
		 (setf name (string-trim '(#\_) 
				   (string-trim '(#\1 #\2 #\3 #\4 #\5 #\6 #\7 #\8 #\9 #\0) 
					 (string (car val)))))                       ;get the name of the object
		 (cond 
		       ((cddr val)                                       ;non boolean!
			     (if (cadr val)                                 ;if there are parameters
				   (progn 
					 (format t "~s~s = ~s~%" (car val) (cadr val) (caddr val))
					 (format ff "~s~s = ~s~%" (car val) (cadr val) (caddr val)))
				   (progn
					 (format t "~s = ~s~%" (car val) (caddr val))
					 (format ff "~s = ~s~%" (car val) (caddr val)))))
		       ((and (null (cddr val))  (null (gethash name *items*)))          ;booleans, then (cddr val) = nil, provided that it is not an *items*
			     (if (cadr val)                                 ;if there are parameters - so booleans defined by "define-tvar using *bool*"
				   (progn
					 (format t "~s~s~%" (car val) (cadr val))
					 (format ff "~s~s~%" (car val) (cadr val)))
				   (progn
					 (if (position #\_ (string (car val)))               ;val is a predicate A_1_2_3
					       (let ((str (concatenate 'string                ;some operations to prepare the string	
								 (substitute #\_ " " (replace (string (car val)) "(" :start1 (position #\_ (string (car val)))))
								 ")~%")))						     
						     (format t str)                                          
						     (format ff str))                                           							   
					       (progn 
						     (format t "~s~%" (car val))
						     (format ff "~s~%" (car val))))
					 )))

		       ((null (gethash name *items*))                     ;other in the case of non-*items*
			     (format t "~s~s~%" (car val) (cadr val)))

		       ((gethash name *items*)               ;manage *items*
		       	     (let ((ogh (gethash name iht)))
		       		   (if ogh               ;if name is an item over a subset
		       			 (setf (gethash name iht) 
		       			       (+ ogh (expt 2 (parse-integer (subseq (string (car val)) (1+ (length name)))))))           ;define the value
		       			 (progn            ;otherwise write the proposition, ogh = nil
		       			       (format t  "  ~s~%" (car val)) 
		       			       (format ff "  ~s~%" (car val))) 
		       			 )
		       		   )
		       	     )
		       )
		 
	     ;manage symbol items - just for **LOOP**
	     ;; when (and (symbolp val) (<= 0 i k))
	     ;; do
	     ;; (setf name (string-trim '(#\_) 
	     ;; 			     (string-trim '(#\1 #\2 #\3 #\4 #\5 #\6 #\7 #\8 #\9 #\0) 
	     ;; 					  (string val))))
	     ;; (let ((ogh (gethash name iht)))
	     ;;   (if ogh               ;if name is an item over a subset
	     ;; 	 (setf (gethash name iht) 
	     ;; 	       (+ ogh (expt 2 (parse-integer (subseq (string val)(1+ (length name)))))))           ;define the value
	     ;; 	 (progn            ;otherwise write the proposition, ogh = nil
	     ;; 	   (format t  "  ~s~%" val) 
	     ;; 	   (format ff "  ~s~%" val)) 
	     ;; 	 )
	     ;;   ))
)
	    (maphash (lambda (x y) 
			   (format t   "  ~S = ~S~%" (intern x) (elt (gethash x *items*) y))			  
			   (format ff  "  ~S = ~S~%" (intern x) (elt (gethash x *items*) y))
			   (setf (gethash x iht) 0)) 
		  iht)

	    finally
	    (format t  "------ end ------~%")
	    (format ff "------ end ------~%")
	    (when unk 
		  (format t  ">>> UNKNOWN <<<") 
		  (format ff ">>> UNKNOWN <<<")) 
	    ))
	))

