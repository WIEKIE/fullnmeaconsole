package nmea.ui.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import java.util.Date;

import javax.swing.JPanel;

import javax.swing.JScrollPane;

import nmea.server.ctx.NMEAContext;
import nmea.server.ctx.NMEADataCache;

import nmea.event.NMEAListener;

import nmea.server.constants.Constants;

import nmea.ui.viewer.elements.SpeedEvolutionDisplay;

import ocss.nmea.parser.Speed;

import ocss.nmea.parser.TrueWindSpeed;

import oracle.jdeveloper.layout.VerticalFlowLayout;


public class SpeedEvolutionPanel
  extends JPanel
{
  private VerticalFlowLayout verticalFlowLayout = new VerticalFlowLayout();

  private SpeedEvolutionDisplay bspLoggingDisplay = new SpeedEvolutionDisplay("BSP", "Boat Speed", 36);
  private SpeedEvolutionDisplay awsLoggingDisplay = new SpeedEvolutionDisplay("AWS", "Apparent Wind Speed", 36);
  private SpeedEvolutionDisplay twsLoggingDisplay = new SpeedEvolutionDisplay("TWS", "True Wind Speed", 36);
  private SpeedEvolutionDisplay sogLoggingDisplay = new SpeedEvolutionDisplay("SOG", "Speed Over Ground", 36);
  private SpeedEvolutionDisplay cspLoggingDisplay = new SpeedEvolutionDisplay("CSP", "Current Speed", 36);
  
  public SpeedEvolutionPanel()
  {
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    this.setSize(new Dimension(450, 350));
    this.setLayout(verticalFlowLayout);
    
    int i = 0;    
    this.add(bspLoggingDisplay, new GridBagConstraints(0, i++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(awsLoggingDisplay, new GridBagConstraints(0, i++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
    this.add(twsLoggingDisplay, new GridBagConstraints(0, i++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
    this.add(sogLoggingDisplay, new GridBagConstraints(0, i++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
    this.add(cspLoggingDisplay, new GridBagConstraints(0, i++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
    
    bspLoggingDisplay.setMax(10d);
    
    awsLoggingDisplay.setMax(70d);
    awsLoggingDisplay.setStep(10d);
    
    twsLoggingDisplay.setMax(70d);
    twsLoggingDisplay.setStep(10d);
    
    sogLoggingDisplay.setMax(10d);
    cspLoggingDisplay.setMax(5d);

    NMEAContext.getInstance().addNMEAListener(new NMEAListener(Constants.NMEA_SERVER_LISTENER_GROUP_ID)
    {
      public void dataUpdate() 
      {
        NMEADataCache cache = NMEAContext.getInstance().getCache();
        if (cache != null)
        {
          Date d = new Date();
          try 
          { 
            bspLoggingDisplay.addValue(d, ((Speed)cache.get(NMEADataCache.BSP)).getValue()); 
            bspLoggingDisplay.addNDValue(d, ((Speed)cache.get(NMEADataCache.BSP, false)).getValue()); 
            bspLoggingDisplay.repaint();
          } 
          catch (Exception ex) {}
          try 
          { 
            awsLoggingDisplay.addValue(d, ((Speed)cache.get(NMEADataCache.AWS)).getValue()); 
            awsLoggingDisplay.addNDValue(d, ((Speed)cache.get(NMEADataCache.AWS, false)).getValue()); 
            awsLoggingDisplay.repaint();
          } 
          catch (Exception ex) {}
          try 
          { 
            twsLoggingDisplay.addValue(d, ((TrueWindSpeed)cache.get(NMEADataCache.TWS)).getValue()); 
            twsLoggingDisplay.addNDValue(d, ((TrueWindSpeed)cache.get(NMEADataCache.TWS, false)).getValue()); 
            twsLoggingDisplay.repaint();
          } 
          catch (Exception ex) {}
          try 
          { 
            sogLoggingDisplay.addValue(d, ((Speed)cache.get(NMEADataCache.SOG)).getValue()); 
            sogLoggingDisplay.addNDValue(d, ((Speed)cache.get(NMEADataCache.SOG, false)).getValue()); 
            sogLoggingDisplay.repaint();
          } 
          catch (Exception ex) {}
          try 
          { 
            cspLoggingDisplay.addValue(d, ((Speed)cache.get(NMEADataCache.CSP)).getValue()); 
            cspLoggingDisplay.addNDValue(d, ((Speed)cache.get(NMEADataCache.CSP, false)).getValue()); 
            cspLoggingDisplay.repaint();
          } 
          catch (Exception ex) {}
        }
      }
    });
  }
}
