package MZex2 
  model MZex2_plant 
    
    annotation (
      uses(Modelica(version="2.2.1")),
      Diagram,
      Icon(Rectangle(extent=[-100,100; 100,-100], style(
            color=0,
            rgbcolor={0,0,0},
            fillColor=30,
            rgbfillColor={215,215,215}))));
    Modelica.Mechanics.Rotational.Inertia Load(J=Jload) 
      annotation (extent=[36,-50; 56,-30]);
    Modelica.Mechanics.Rotational.Torque Motor 
      annotation (extent=[-44,-50; -24,-30]);
    Modelica.Mechanics.Rotational.SpringDamper Joint(c=keJoint, d=dJoint) 
      annotation (extent=[10,-50; 30,-30]);
    Modelica.Blocks.Continuous.LimPID PIwMot(
      controllerType=Modelica.Blocks.Types.SimpleController.PI,
      k=K_PIwMot,
      Ti=Ti_PIwMot,
      yMax=tauMotMax,
      yMin=-tauMotMax) annotation (extent=[-10,24; -30,44]);
    Modelica.Mechanics.Rotational.Sensors.AngleSensor phiLoad 
      annotation (extent=[48,-24; 68,-4],rotation=90);
    Modelica.Mechanics.Rotational.Sensors.SpeedSensor wMot 
      annotation (extent=[-30,-24; -10,-4],rotation=90);
    Modelica.Mechanics.Rotational.Fixed fixed 
      annotation (extent=[-44,-78; -24,-58]);
    Modelica.Mechanics.Rotational.IdealGear Gbox(ratio=GBratio) 
      annotation (extent=[-16,-50; 4,-30]);
    Modelica.Blocks.Math.Product PphiLoad annotation (extent=[22,24; 2,44]);
    Modelica.Blocks.Math.Feedback FBphiLoad annotation (extent=[68,18; 48,38]);
    Modelica.Blocks.Interfaces.RealOutput o_wMot 
      annotation (extent=[80,40; 120,80]);
    Modelica.Blocks.Interfaces.RealOutput o_phiLoad 
      annotation (extent=[80,-20; 120,20]);
    Modelica.Blocks.Interfaces.RealInput i_SPphiLoad 
      annotation (extent=[-120,20; -80,60]);
    Modelica.Blocks.Interfaces.RealInput i_gainPphiLoad 
      annotation (extent=[-120,-60; -80,-20]);
    parameter Real tauMotMax=50 "Max motor torque magnitude";
    parameter Modelica.SIunits.Time Tmot=0.05 "Motoe command TC";
    parameter Real K_PIwMot=2 "Motor speed PI gain";
    parameter Modelica.SIunits.Time Ti_PIwMot=0.4 
      "Motor speed PI integral time";
    parameter Real GBratio=5 "Gearbox ratio";
    parameter Real keJoint=100 "Joint elastic constant";
    parameter Real dJoint=0.05 "Joint damping constant";
    parameter Modelica.SIunits.Inertia Jload=0.1 "Load inertia";
    Modelica.Blocks.Continuous.FirstOrder MotCmd(T=Tmot) 
      annotation (extent=[-62,0; -42,20], rotation=270);
    Modelica.Blocks.Interfaces.RealOutput o_tauMot 
      annotation (extent=[80,-80; 120,-40]);
  equation 
    connect(fixed.flange_b, Motor.bearing) 
      annotation (points=[-34,-68; -34,-50], style(color=0, rgbcolor={0,0,0}));
    connect(Motor.flange_b, Gbox.flange_a) 
      annotation (points=[-24,-40; -16,-40], style(color=0, rgbcolor={0,0,0}));
    connect(Gbox.flange_b, Joint.flange_a) 
      annotation (points=[4,-40; 10,-40], style(color=0, rgbcolor={0,0,0}));
    connect(Gbox.bearing, fixed.flange_b) annotation (points=[-6,-50; -6,-56; 
          -34,-56; -34,-68], style(color=0, rgbcolor={0,0,0}));
    connect(Joint.flange_b, Load.flange_a) 
      annotation (points=[30,-40; 36,-40], style(color=0, rgbcolor={0,0,0}));
    connect(Motor.flange_b, wMot.flange_a) annotation (points=[-24,-40; -20,-40; 
          -20,-24], style(color=0, rgbcolor={0,0,0}));
    connect(PIwMot.u_s, PphiLoad.y) 
      annotation (points=[-8,34; 1,34],  style(color=74, rgbcolor={0,0,127}));
    connect(wMot.w, PIwMot.u_m) 
      annotation (points=[-20,-3; -20,22],style(color=74, rgbcolor={0,0,127}));
    connect(phiLoad.phi, FBphiLoad.u2) 
      annotation (points=[58,-3; 58,20],style(color=74, rgbcolor={0,0,127}));
    connect(PphiLoad.u2, FBphiLoad.y) 
      annotation (points=[24,28; 49,28], style(color=74, rgbcolor={0,0,127}));
    connect(wMot.w, o_wMot) annotation (points=[-20,-3; -20,8; 76,8; 76,60; 100,
          60], style(color=74, rgbcolor={0,0,127}));
    connect(phiLoad.phi, o_phiLoad) annotation (points=[58,-3; 58,0; 76,0; 76,
          1.11022e-15; 100,1.11022e-15],
                         style(color=74, rgbcolor={0,0,127}));
    connect(PphiLoad.u1, i_gainPphiLoad) annotation (points=[24,40; 30,40; 30,
          52; -72,52; -72,-40; -100,-40], style(color=74, rgbcolor={0,0,127}));
    connect(i_SPphiLoad, FBphiLoad.u1) annotation (points=[-100,40; -76,40; -76,
          60; 70,60; 70,28; 66,28], style(color=74, rgbcolor={0,0,127}));
    connect(Load.flange_b, phiLoad.flange_a) annotation (points=[56,-40; 58,-40; 
          58,-24], style(color=0, rgbcolor={0,0,0}));
    connect(PIwMot.y, MotCmd.u) annotation (points=[-31,34; -52,34; -52,22],
        style(color=74, rgbcolor={0,0,127}));
    connect(MotCmd.y, Motor.tau) annotation (points=[-52,-1; -52,-40; -46,-40],
        style(color=74, rgbcolor={0,0,127}));
    connect(MotCmd.y, o_tauMot) annotation (points=[-52,-1; -52,-84; 68,-84; 68,
          -60; 100,-60], style(color=74, rgbcolor={0,0,127}));
  end MZex2_plant;
  annotation (uses(Modelica(version="2.2.1")));
  model test_MZex2_plant 
    MZex2_plant mZex2_plant annotation (extent=[-22,0; -2,20]);
    Modelica.Blocks.Sources.Trapezoid SPphiLoad(
      startTime=1,
      amplitude=0.2,
      rising=5,
      width=5,
      falling=5,
      period=20,
      offset=0) annotation (extent=[-76,4; -56,24]);
    annotation (
      Diagram,
      experiment(StopTime=400),
      experimentSetupOutput);
    Modelica.Blocks.Sources.Sine GainPphiLoad(
      amplitude=4,
      freqHz=1/200,
      offset=5) annotation (extent=[-72,-40; -52,-20]);
  equation 
    connect(SPphiLoad.y, mZex2_plant.i_SPphiLoad) annotation (points=[-55,14;
          -22,14], style(
        color=74,
        rgbcolor={0,0,127},
        fillColor=30,
        rgbfillColor={215,215,215},
        fillPattern=1));
    connect(GainPphiLoad.y, mZex2_plant.i_gainPphiLoad) 
                                                annotation (points=[-51,-30;
          -44,-30; -44,6; -22,6], style(
        color=74,
        rgbcolor={0,0,127},
        fillColor=30,
        rgbfillColor={215,215,215},
        fillPattern=1));
  end test_MZex2_plant;
end MZex2;
