within;
package MZex2
  model MZex2_plant

    Modelica.Mechanics.Rotational.Components.Inertia Load(
                                               J=Jload)
      annotation (Placement(transformation(extent={{36,-50},{56,-30}}, rotation=0)));
    Modelica.Mechanics.Rotational.Sources.Torque Motor(useSupport=true)
      annotation (Placement(transformation(extent={{-44,-50},{-24,-30}}, rotation=
             0)));
    Modelica.Mechanics.Rotational.Components.SpringDamper Joint(
                                                     c=keJoint, d=dJoint)
      annotation (Placement(transformation(extent={{10,-50},{30,-30}}, rotation=0)));
    Modelica.Blocks.Continuous.LimPID PIwMot(
      controllerType=Modelica.Blocks.Types.SimpleController.PI,
      k=K_PIwMot,
      Ti=Ti_PIwMot,
      yMax=tauMotMax,
      yMin=-tauMotMax) annotation (Placement(transformation(extent={{-10,24},{-30,
              44}}, rotation=0)));
    Modelica.Mechanics.Rotational.Sensors.AngleSensor phiLoad
      annotation (Placement(transformation(
          origin={58,-14},
          extent={{-10,-10},{10,10}},
          rotation=90)));
    Modelica.Mechanics.Rotational.Sensors.SpeedSensor wMot
      annotation (Placement(transformation(
          origin={-20,-14},
          extent={{-10,-10},{10,10}},
          rotation=90)));
    Modelica.Mechanics.Rotational.Components.Fixed fixed
      annotation (Placement(transformation(extent={{-44,-78},{-24,-58}}, rotation=
             0)));
    Modelica.Mechanics.Rotational.Components.IdealGear Gbox(
                                                 ratio=GBratio, useSupport=true)
      annotation (Placement(transformation(extent={{-16,-50},{4,-30}}, rotation=0)));
    Modelica.Blocks.Math.Product PphiLoad annotation (Placement(transformation(
            extent={{22,24},{2,44}}, rotation=0)));
    Modelica.Blocks.Math.Feedback FBphiLoad annotation (Placement(transformation(
            extent={{68,18},{48,38}}, rotation=0)));
    Modelica.Blocks.Continuous.FirstOrder MotCmd(T=Tmot)
      annotation (Placement(transformation(
          origin={-52,10},
          extent={{-10,-10},{10,10}},
          rotation=270)));
    parameter Real tauMotMax=50 "Max motor torque magnitude";
    parameter Modelica.SIunits.Time Tmot=0.05 "Motoe command TC";
    parameter Real K_PIwMot=2 "Motor speed PI gain";
    parameter Modelica.SIunits.Time Ti_PIwMot=0.4
      "Motor speed PI integral time";
    parameter Real GBratio=5 "Gearbox ratio";
    parameter Real keJoint=100 "Joint elastic constant";
    parameter Real dJoint=0.05 "Joint damping constant";
    parameter Modelica.SIunits.Inertia Jload=0.1 "Load inertia";
    //-----------------------------------------------
    // output of the system
    Modelica.Blocks.Interfaces.RealOutput o_tauMot
      annotation (Placement(transformation(extent={{80,-80},{120,-40}}, rotation=0)));
    Modelica.Blocks.Interfaces.RealOutput o_wMot
      annotation (Placement(transformation(extent={{80,40},{120,80}}, rotation=0)));
    Modelica.Blocks.Interfaces.RealOutput o_phiLoad
      annotation (Placement(transformation(extent={{80,-20},{120,20}}, rotation=0)));
    //-----------------------------------------------
    //-----------------------------------------------
    // inputs for the system
    Real i_SPphiLoad;
    Real i_gainPphiLoad;
    //-----------------------------------------------
    //-----------------------------------------------
    // parameters that represents the inputs
    parameter Real SP_phi = 0;
    parameter Real gainP = 1;
    //-----------------------------------------------
    // definition of tresholds for the output variables
    parameter Real TR_phiLoad = 6;
    // definition of discrete variables that identify the variations
    discrete Integer s;
    //-----------------------------------------------
  algorithm
    //-----------------------------------------------
    // initialization of the output file
    when initial() then
      Init();
    end when;
    //-----------------------------------------------
    //-----------------------------------------------
    // treshold for output 1
    when o_phiLoad > TR_phiLoad then
      s := 1;
    elsewhen o_phiLoad <= TR_phiLoad then
      s := 0;
    end when;

    when change(s) then
      FilePrint(s,pre(s),time);
    end when;
    //-----------------------------------------------
  equation
    // assign the parameters to the input variables
    i_SPphiLoad = SP_phi;
    i_gainPphiLoad = gainP;

    // input values
    PphiLoad.u1 = i_gainPphiLoad;
    i_SPphiLoad = FBphiLoad.u1;

    // connections
    connect(fixed.flange,Motor.support)
      annotation (Line(points={{-34,-68},{-34,-50}}, color={0,0,0}));
    connect(Motor.flange,   Gbox.flange_a)
      annotation (Line(points={{-24,-40},{-16,-40}}, color={0,0,0}));
    connect(Gbox.flange_b, Joint.flange_a)
      annotation (Line(points={{4,-40},{10,-40}}, color={0,0,0}));
    connect(Gbox.support,fixed.flange)    annotation (Line(points={{-6,-50},{-6,
            -56},{-34,-56},{-34,-68}},
                                  color={0,0,0}));
    connect(Joint.flange_b, Load.flange_a)
      annotation (Line(points={{30,-40},{36,-40}}, color={0,0,0}));
    connect(Motor.flange,wMot.flange)      annotation (Line(points={{-24,-40},{-20,
            -40},{-20,-24}}, color={0,0,0}));
    connect(PIwMot.u_s, PphiLoad.y)
      annotation (Line(points={{-8,34},{1,34}}, color={0,0,127}));
    connect(wMot.w, PIwMot.u_m)
      annotation (Line(points={{-20,-3},{-20,22}}, color={0,0,127}));
    connect(phiLoad.phi, FBphiLoad.u2)
      annotation (Line(points={{58,-3},{58,20}}, color={0,0,127}));
    connect(PphiLoad.u2, FBphiLoad.y)
      annotation (Line(points={{24,28},{49,28}}, color={0,0,127}));
    connect(wMot.w, o_wMot) annotation (Line(points={{-20,-3},{-20,8},{76,8},{
            76,60},{100,60}},
                       color={0,0,127}));
    connect(phiLoad.phi, o_phiLoad) annotation (Line(points={{58,-3},{58,0},{76,
            0},{76,1.11022e-15},{100,1.11022e-15}},
                                                 color={0,0,127}));
    connect(Load.flange_b,phiLoad.flange)    annotation (Line(points={{56,-40},{58,
            -40},{58,-24}}, color={0,0,0}));
    connect(PIwMot.y, MotCmd.u) annotation (Line(points={{-31,34},{-52,34},{-52,
            22}},
          color={0,0,127}));
    connect(MotCmd.y, Motor.tau) annotation (Line(points={{-52,-1},{-52,-40},{
            -46,-40}},
                   color={0,0,127}));
    connect(MotCmd.y, o_tauMot) annotation (Line(points={{-52,-1},{-52,-84},{68,
            -84},{68,-60},{100,-60}},
                                 color={0,0,127}));
    annotation (
      uses(Modelica(version="2.2.1")),
      Diagram(graphics),
      Icon(graphics));
  end MZex2_plant;

    function Init
      external"C"
       InitExt() annotation(Library="libInitExt.o",Include="#include \"InitExt.h\"");
    end Init;

    function FilePrint
     input Real x;
     input Real x_pre;
     input Real t;
    external"C"
     PrintExt(x,x_pre,t) annotation(Library="libPrintExt.o",Include="#include \"PrintExt.h\"");
    end FilePrint;

  annotation (uses(Modelica(version="3.1")),
    version="1",
    conversion(from(version="", script="ConvertFromMZex2_.mos")));

end MZex2;
