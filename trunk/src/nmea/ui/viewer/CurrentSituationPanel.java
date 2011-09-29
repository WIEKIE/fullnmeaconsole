package nmea.ui.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import nmea.server.ctx.NMEAContext;
import nmea.server.ctx.NMEADataCache;
import nmea.server.utils.Utils;

import nmea.event.NMEAListener;

import nmea.server.constants.Constants;

import nmea.ui.viewer.elements.AWDisplay;
import nmea.ui.viewer.elements.CurrentDisplay;
import nmea.ui.viewer.elements.DrawingBoard;
import nmea.ui.viewer.elements.HeadingPanel;
import nmea.ui.viewer.elements.JumboDisplay;
import nmea.ui.viewer.minimaxi.boatspeed.BoatSpeed;
import nmea.ui.viewer.minimaxi.wind.WindSpeed;

import ocss.nmea.parser.Angle180;
import ocss.nmea.parser.Angle180LR;
import ocss.nmea.parser.Angle360;
import ocss.nmea.parser.Speed;
import ocss.nmea.parser.TrueWindDirection;
import ocss.nmea.parser.TrueWindSpeed;
import ocss.nmea.utils.WindUtils;

public class CurrentSituationPanel
  extends JPanel
{
  private CurrentSituationPanel instance = this;
  
  private JPanel drawingPlusCompass = new JPanel();
  private DrawingBoard drawingBoard = new DrawingBoard();
  private JPanel compasPanel = new JPanel();
  private HeadingPanel hdgPanel = new HeadingPanel();
  private HeadingPanel cogPanel = new HeadingPanel();
  private JScrollPane rightScrollPane = null;
  private JPanel rightPanel = new JPanel();
  private JPanel displayPanel = new JPanel();
  private JPanel topDisplayPanel = new JPanel();
  private JumboDisplay bspDisplay = null;
  private JumboDisplay hdgDisplay = null;
  private JumboDisplay awaDisplay = null;
  private JumboDisplay awsDisplay = null;
  private JumboDisplay twaDisplay = null;
  private JumboDisplay twsDisplay = null;
  private JumboDisplay twdDisplay = null;
  private JumboDisplay lwyDisplay = null;
  private JumboDisplay cdrDisplay = null;
  private JumboDisplay cspDisplay = null;
  private JumboDisplay cogDisplay = null;
  private JumboDisplay sogDisplay = null;
  private JumboDisplay beaufortDisplay = null;
  
  private int basicJumboSize = 24;
  
  private AWDisplay awDisplay           = null;
  private CurrentDisplay currentDisplay = null;
  
  private BoatSpeed bspMinMaxPanel = new BoatSpeed(basicJumboSize);
  private WindSpeed twsMinMaxPanel = new WindSpeed(basicJumboSize);
  
  private DecimalFormat df3 = new DecimalFormat("000");
  private DecimalFormat df2 = new DecimalFormat("#0");
  private DecimalFormat df22 = new DecimalFormat("00.00");
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel hdgLabel = new JLabel();
  private JLabel cogLabel = new JLabel();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  
  private JCheckBox showLeftPaneCheckBox = new JCheckBox();
  private JCheckBox miniMaxiCheckBox = new JCheckBox();
  private JCheckBox showTemperatureCheckBox = new JCheckBox();

  private JCheckBox displayCurrentCheckBox = new JCheckBox();
  
  private JPanel twsMethodPanel = new JPanel();
  private JLabel twLabel = new JLabel("True Wind:");
  private JRadioButton gpsMethod = new JRadioButton("With GPS Data");
  private JRadioButton bspMethod = new JRadioButton("With BSP & AW");
  private ButtonGroup twsMethodGroup = new ButtonGroup();

  private boolean frozen = false;
  private ImageIcon pause = new ImageIcon(this.getClass().getResource("elements/resources/pause.png"));
  private ImageIcon start = new ImageIcon(this.getClass().getResource("elements/resources/start.png"));
  private JButton freezeButton = new JButton();
  private JButton shiftLeftRightButton = new JButton();
  private ImageIcon left  = new ImageIcon(this.getClass().getResource("elements/resources/shuttleLeftAll.png"));
  private ImageIcon right = new ImageIcon(this.getClass().getResource("elements/resources/shuttleRightAll.png"));
  
  private boolean jumbosOnTheRight = true;

  public CurrentSituationPanel()
  {
    try
    {
      jbInit();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    NMEAContext.getInstance().addNMEAListener(new NMEAListener(Constants.NMEA_SERVER_LISTENER_GROUP_ID)
      {
        public void dataUpdate() 
        {
          NMEADataCache cache = NMEAContext.getInstance().getCache();
          if (frozen)
            cache = NMEAContext.getInstance().getFrozenDataCache();
          if (cache != null)
          {
            try 
            { 
              double hdg = ((Angle360)cache.get(NMEADataCache.HDG_TRUE)).getValue() + ((Double)cache.get(NMEADataCache.HDG_OFFSET)).doubleValue();              
              setHDG(hdg);               
              hdgPanel.setValue(hdg);
            } 
            catch (Exception ex) {}
            try { setBSP(((Speed)cache.get(NMEADataCache.BSP)).getValue() * ((Double)cache.get(NMEADataCache.BSP_FACTOR)).doubleValue()); } catch (Exception ex) {}
            try { setSOG(((Speed)cache.get(NMEADataCache.SOG)).getValue()); } catch (Exception ex) {}
            try 
            { 
              double cog = ((Angle360)cache.get(NMEADataCache.COG)).getValue();
              setCOG(cog); 
              cogPanel.setValue(cog);
            } 
            catch (Exception ex) {}
            
            try { setAWA(((Angle180)cache.get(NMEADataCache.AWA)).getValue() + ((Double)cache.get(NMEADataCache.AWA_OFFSET)).doubleValue()); } catch (Exception ex) {}
            try { setAWS(((Speed)cache.get(NMEADataCache.AWS)).getValue() * ((Double)cache.get(NMEADataCache.AWS_FACTOR)).doubleValue()); } catch (Exception ex) {}
            
            if (frozen)
            {
              // Recalculated Data - if coeffs have been changed
              Utils.computeAndSendValuesToCache(cache);
            }
            
            try { setTWS(((TrueWindSpeed)cache.get(NMEADataCache.TWS)).getValue()); } catch (Exception ex) {}
            try { setTWA(((Angle180)cache.get(NMEADataCache.TWA)).getValue()); } catch (Exception ex) {}
            try { setTWD(((TrueWindDirection)cache.get(NMEADataCache.TWD)).getValue()); } catch (Exception ex) {}
            try { setLWY(((Angle180LR)cache.get(NMEADataCache.LEEWAY)).getValue()); } catch (Exception ex) {}
            try { setCDR(((Angle360)cache.get(NMEADataCache.CDR)).getValue()); } catch (Exception ex) {}
            try { setCSP(((Speed)cache.get(NMEADataCache.CSP)).getValue()); } catch (Exception ex) {}
          }
          repaint();
        } 
      });

    this.setLayout(new BorderLayout());
    drawingPlusCompass.setLayout(new BorderLayout());
    compasPanel.setLayout(gridBagLayout1);
    hdgPanel.setSize(new Dimension(428, 40));
    hdgPanel.setPreferredSize(new Dimension(200, 30));
    hdgPanel.setMinimumSize(new Dimension(200, 30));
    cogPanel.setPreferredSize(new Dimension(200, 30));
    cogPanel.setMinimumSize(new Dimension(200, 30));
    cogPanel.setWhiteOnBlack(false);
    drawingPlusCompass.add(drawingBoard, BorderLayout.CENTER);
    
    compasPanel.add(hdgPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(1, 1, 1, 1), 0, 0));
    compasPanel.add(cogPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(1, 1, 1, 1), 0, 0));
    compasPanel.add(hdgLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    compasPanel.add(cogLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    drawingPlusCompass.add(compasPanel, BorderLayout.SOUTH);
    this.setSize(new Dimension(800, 600));
    this.setPreferredSize(new Dimension(800, 600));
    this.add(drawingPlusCompass, BorderLayout.CENTER);
    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(displayPanel, BorderLayout.NORTH);
    rightScrollPane = new JScrollPane(rightPanel);
//  rightScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//  this.add(rightPanel, BorderLayout.EAST);    
    this.add(rightScrollPane, BorderLayout.EAST);    
    
    displayPanel.setLayout(gridBagLayout2);
    topDisplayPanel.setLayout(new GridBagLayout());
    displayPanel.add(topDisplayPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

    
    displayPanel.add(showLeftPaneCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));
    displayPanel.add(miniMaxiCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));
    displayPanel.add(showTemperatureCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));
    displayPanel.add(displayCurrentCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));
    displayPanel.add(twsMethodPanel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(freezeButton, null);
    buttonPanel.add(shiftLeftRightButton, null);
    displayPanel.add(buttonPanel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));

    bspDisplay = new JumboDisplay("BSP", "00.00", "Boat Speed", basicJumboSize);
    hdgDisplay = new JumboDisplay("HDG", "000",   "True Heading", basicJumboSize);
    awaDisplay = new JumboDisplay("AWA", "000",   "Apparent Wind Angle", basicJumboSize);
    awsDisplay = new JumboDisplay("AWS", "00.00", "Apparent Wind Speed", basicJumboSize);
    sogDisplay = new JumboDisplay("SOG", "00.00", "Speed Over Ground", basicJumboSize);
    cogDisplay = new JumboDisplay("COG", "000",   "Course Over Ground", basicJumboSize);

    twaDisplay = new JumboDisplay("TWA", "000",   "True Wind Angle", basicJumboSize);
    twaDisplay.setDisplayColor(Color.cyan);
    twsDisplay = new JumboDisplay("TWS", "00.00", "True Wind Speed", basicJumboSize);
    twsDisplay.setDisplayColor(Color.cyan);
    twdDisplay = new JumboDisplay("WINDIR", "000", "True Wind Direction", basicJumboSize); // TWD
    twdDisplay.setDisplayColor(Color.cyan);

    lwyDisplay = new JumboDisplay("LWY", "00.00", "Leeway", basicJumboSize);
    lwyDisplay.setDisplayColor(Color.red);

    cspDisplay = new JumboDisplay("CSP", "00.00", "Current Speed", basicJumboSize);
    cspDisplay.setDisplayColor(Color.cyan);
    cdrDisplay = new JumboDisplay("CDR", "000",   "Current Direction", basicJumboSize);
    cdrDisplay.setDisplayColor(Color.cyan);
    
    awDisplay      = new AWDisplay("Wind", "00.00", "Apparent Wind", basicJumboSize);
    currentDisplay = new CurrentDisplay("Current", "00.00", "Current", basicJumboSize);
    currentDisplay.setDisplayColor(Color.cyan);

    beaufortDisplay = new JumboDisplay("Beaufort", "F 0", "True Wind Speed", basicJumboSize);
    beaufortDisplay.setDisplayColor(Color.cyan);

    hdgLabel.setText("HDG (t):");
    cogLabel.setText("COG:");
    
    showLeftPaneCheckBox.setText("Show 2D Pane");
    showLeftPaneCheckBox.setSelected(true);
    showLeftPaneCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          showLeftPaneCheckBox_actionPerformed(e);
        }
      });

    miniMaxiCheckBox.setText("Show mini & maxi");
    miniMaxiCheckBox.setSelected(true);
    miniMaxiCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          miniMaxiCheckBox_actionPerformed(e);
        }
      });
    
    showTemperatureCheckBox.setText("Show Water Temperature");
    
    boolean dt = "true".equals(System.getProperty("display.temperature", "false"));
    showTemperatureCheckBox.setSelected(dt);
    drawingBoard.setShowTemperature(dt);
    showTemperatureCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          drawingBoard.setShowTemperature(showTemperatureCheckBox.isSelected());
          System.setProperty("display.temperature", showTemperatureCheckBox.isSelected()?"true":"false");
        }
      });
    displayCurrentCheckBox.setText("Display Current");
    displayCurrentCheckBox.setSelected(true);
    displayCurrentCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          displayCurrentCheckBox_actionPerformed(e);
        }
      });
    
    twsMethodPanel.add(twLabel, null);
    twsMethodPanel.add(gpsMethod, null);
    twsMethodPanel.add(bspMethod, null);
    twsMethodGroup.add(gpsMethod);
    twsMethodGroup.add(bspMethod);
    gpsMethod.setSelected(true);
    bspMethod.setSelected(false);
    System.setProperty("use.gps.method", "true");
    gpsMethod.addActionListener(new ActionListener()
                                {
                                  public void actionPerformed(ActionEvent e)
                                  {
                                    if (gpsMethod.isSelected())
                                      System.setProperty("use.gps.method", "true");
                                    else
                                      System.setProperty("use.gps.method", "false");
                                  }
                                });
    bspMethod.addActionListener(new ActionListener()
                                {
                                  public void actionPerformed(ActionEvent e)
                                  {
                                    if (bspMethod.isSelected())
                                      System.setProperty("use.gps.method", "false");
                                    else
                                      System.setProperty("use.gps.method", "true");
                                  }
                                });
    
    freezeButton.setPreferredSize(new Dimension(24, 24));
    freezeButton.setBorderPainted(false);
    freezeButton.setIcon(pause);
    freezeButton.setToolTipText("<html>Freeze the display so<br>you can adjust coefficients</html>");
    freezeButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          freezeButton_actionPerformed(e);
        }
      });
    
    shiftLeftRightButton.setPreferredSize(new Dimension(24, 24));
    shiftLeftRightButton.setBorderPainted(false);
    shiftLeftRightButton.setIcon(left);
    shiftLeftRightButton.setToolTipText("Shift the all panel left");
    shiftLeftRightButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          if (jumbosOnTheRight) // Then shift left
          {
            instance.remove(rightScrollPane);
            instance.add(rightScrollPane, BorderLayout.WEST);
            shiftLeftRightButton.setIcon(right);
            shiftLeftRightButton.setToolTipText("Shift the all panel right");
            jumbosOnTheRight = false;
          }
          else // Then shift right
          {
            instance.remove(rightScrollPane);
            instance.add(rightScrollPane, BorderLayout.EAST);
            shiftLeftRightButton.setIcon(left);
            shiftLeftRightButton.setToolTipText("Shift the all panel left");
            jumbosOnTheRight = true;
          }
        }
      });
      
    topDisplayPanel.add(bspDisplay, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(hdgDisplay, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(awaDisplay, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(awsDisplay, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(cogDisplay, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(sogDisplay, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(awDisplay, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    
    topDisplayPanel.add(twdDisplay, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(lwyDisplay, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(twaDisplay, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(twsDisplay, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(cdrDisplay, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(cspDisplay, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(currentDisplay, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

    topDisplayPanel.add(bspMinMaxPanel, new GridBagConstraints(2, 0, 1, 3, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(beaufortDisplay, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
    topDisplayPanel.add(twsMinMaxPanel, new GridBagConstraints(2, 4, 1, 3, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(1, 1, 1, 1), 0, 0));
    // Init values
//  hdgPanel.setHdg(53);
//  cogPanel.setHdg(94);
  }

  private void displayCurrentCheckBox_actionPerformed(ActionEvent e)
  {
    setDisplayCurrent(displayCurrentCheckBox.isSelected());
  }

  private void freezeButton_actionPerformed(ActionEvent e)
  {
    frozen = !frozen;
    if (frozen) // Take Snapshot
      NMEAContext.getInstance().setFrozenDataCache(NMEAContext.getInstance().cloneCache());
    else
      NMEAContext.getInstance().setFrozenDataCache(null);
    ImageIcon icon = (frozen?start:pause);
    freezeButton.setIcon(icon);
    freezeButton.setToolTipText(frozen?"Resume display":"<html>Freeze the display so<br>you can adjust coefficients</html>");

    freezeButton.repaint();
    setFreeze(frozen);
  }

  private void setBSP(double d)
  {
    if (d != -Double.MAX_VALUE)
      bspDisplay.setValue(df22.format(d));
  }

  private void setHDG(double d)
  {
    hdgDisplay.setValue(df3.format(d));
  }

  private void setAWA(double d)
  {
    String str = df3.format(d);
    if (d > 0)
      str += "-";
    awaDisplay.setValue(str);
    awDisplay.setAWA(d);
  }

  private void setAWS(double d)
  {
    if (d != -Double.MAX_VALUE)
    {
      awsDisplay.setValue(df22.format(d));
      awDisplay.setAWS(d);
    }
  }

  private void setTWA(double d)
  {
    String str = df3.format(d);
    if (d > 0)
      str += "-";
    twaDisplay.setValue(str);
  }

  private void setTWD(double d)
  {
    twdDisplay.setValue(df3.format(d));
  }

  private void setTWS(double d)
  {
    if (d != -Double.MAX_VALUE && !Double.isInfinite(d) && !Double.isNaN(d))
    {
      twsDisplay.setValue(df22.format(d));
      beaufortDisplay.setValue("F " + df2.format(WindUtils.getBeaufort(d)));
    }
  }

  private void setCSP(double d)
  {
    if (d != -Double.MAX_VALUE && !Double.isInfinite(d) && !Double.isNaN(d))
    {
      cspDisplay.setValue(df22.format(d));
      currentDisplay.setSpeed(d);
    }
  }

  private void setCDR(double d)
  {
    if (d != -Double.MAX_VALUE && !Double.isInfinite(d) && !Double.isNaN(d))
    {
      cdrDisplay.setValue(df3.format(d));
      currentDisplay.setDirection(d);
    }
  }

  private void setLWY(double d)
  {
    lwyDisplay.setValue(df22.format(d));
  }

  private void setCOG(double d)
  {
    cogDisplay.setValue(df3.format(d));
  }

  private void setSOG(double d)
  {
    sogDisplay.setValue(df22.format(d));
  }
  
  public void setDisplayCurrent(boolean b)
  {
    try { drawingBoard.setDisplayCurrent(b); } catch (Exception ex) {}
  }

  public void setFreeze(boolean b)
  {
    try { drawingBoard.setFreeze(b); } catch (Exception ex) {}
    freezeButton.setToolTipText(b?"Resume":"Freeze");
  }

  private void miniMaxiCheckBox_actionPerformed(ActionEvent e)
  {
    bspMinMaxPanel.setVisible(miniMaxiCheckBox.isSelected());
    twsMinMaxPanel.setVisible(miniMaxiCheckBox.isSelected());
    beaufortDisplay.setVisible(miniMaxiCheckBox.isSelected());
  }
  
  private void showLeftPaneCheckBox_actionPerformed(ActionEvent e)
  {
    drawingPlusCompass.setVisible(showLeftPaneCheckBox.isSelected());
  }
}