package nmea.ui;

import astro.calc.GeoPoint;

import coreutilities.Utilities;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import nmea.event.NMEAReaderListener;

import nmea.server.constants.Constants;

import nmea.server.ctx.NMEAContext;
import nmea.server.ctx.NMEADataCache;

import nmea.ui.widgets.LoggedDataTable;

import nmea.ui.widgets.PositionPanel;

import ocss.nmea.parser.GeoPos;
import ocss.nmea.parser.OverGround;
import ocss.nmea.parser.RMC;

import ocss.nmea.parser.StringParsers;
import ocss.nmea.parser.Wind;

import utils.NMEAAnalyzer;

import utils.NMEAAnalyzer.AirTemp;
import utils.NMEAAnalyzer.AtmPressure;
import utils.NMEAAnalyzer.ScalarValue;

import utils.NMEAAnalyzer.WaterTemp;

import utils.log.LogAnalysis;
import utils.log.LoggedDataSelectedInterface;


public class NMEAAnalyzerLandingPanel 
     extends JPanel
  implements LoggedDataSelectedInterface
{
  @SuppressWarnings("compatibility:-4182475730283721677")
  public final static long serialVersionUID = 1L;

  private GridBagLayout layout  = new GridBagLayout();
  private JLabel fileInLabel = new JLabel();
  private JLabel fileOutLabel = new JLabel();
  private JTextField fileInTextField = new JTextField();
  private JButton browseFileInButton = new JButton();
  private JButton parseButton = new JButton();
  private LoggedDataTable loggedDataTable = null;
  private transient NMEAAnalyzer na = null;
  private transient LogAnalysis[] laa = null;
  private PositionPanel positionPanel = new PositionPanel();
  private JPanel radioButtonPanel = new JPanel();
  private ButtonGroup group = new ButtonGroup();
  private JRadioButton gpsRadioButton = new JRadioButton();
  private JRadioButton manualRadioButton = new JRadioButton();
  private JComboBox timeZoneComboBox = new JComboBox();
  private JTextField narrowTextField = new JTextField();
  private JPanel tzPanel = new JPanel();

  public NMEAAnalyzerLandingPanel()
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

  @Override
  public void setSelectedData(String data, boolean b)
  {
    System.out.println("Selected " + data + ", " + b);
    Map<String, Map<Date, Object>> fullData = na.getFullData();
    Set<String> keys = fullData.keySet();
    int idx = -1, i = 0;
    for (String k : keys)
    {
      if (data.equals(k))
      {
        idx = i;
        break;
      }
      else
        i++;
    }
    if (idx == -1) // Not found, weird
    {
      JOptionPane.showMessageDialog(this, "Requested data [" + data + "] not found in the log");
      return;
    }
    else
    {
      Map<Date, ScalarValue> newData = null;
      String newTitle = "Data Title";
      String newUnit = "?";
      if (b && laa[idx] == null)
      {
       /* VWR: Wind
        * MWV: Wind
        * RMC: RMC
        * RMB: RMB
        * BAT: float
        * HDG: double[] [HDG_in_HDG, DEV_in_HDG, VAR_in_HDG]
        * VLW: double[] [LOG_in_VLW, DAILYLOG_in_VLW]
        * VHW: double
        * MTW: double
        * MTA: double
        * MMB: double
        * DPT: float
        * GLL: Object[] [GeoPos, Date]
        * VTG: OverGround
        */
        if ("BAT".equals(data))
        {
          newTitle = "Battery Voltage";
          newUnit  = "V";
        }
        else if ("DPT".equals(data))
        {
          newTitle = "Depth";
          newUnit  = "m";
        }
        else if ("VHW".equals(data))
        {
          newTitle = "Speed Through Water";
          newUnit  = "kt";
        }
        else if ("VWR".equals(data) || "MWV".equals(data))
        {
          newTitle = "Apparent Wind Speed";
          newUnit  = "kt";
        }
        else if ("MTW".equals(data))
        {
          newTitle = "Water Temperature";
          newUnit  = "\272C";
        }
        else if ("MTA".equals(data))
        {
          newTitle = "Air Temperature";
          newUnit  = "\272C";
        }
        else if ("MMB".equals(data))
        {
          newTitle = "Atmospheric Pressure";
          newUnit  = "hPa";
        }
        else if ("RMC".equals(data) || "VTG".equals(data))
        {
          newTitle = "Speed over Ground";
          newUnit  = "kt";
        }
        newData = new TreeMap<Date, ScalarValue>();
        Map<Date, Object> datamap = fullData.get(data);
        Set<Date> dates = datamap.keySet();
        for (Date d : dates)
        {
          if ("BAT".equals(data))
          {            
            NMEAAnalyzer.BatteryVoltage bv = (NMEAAnalyzer.BatteryVoltage)datamap.get(d);
            newData.put(d, bv);
          }
          else if ("DPT".equals(data))
          {
            NMEAAnalyzer.Depth depth = (NMEAAnalyzer.Depth)datamap.get(d);
            newData.put(d, depth);
          }
          else if ("RMC".equals(data))
          {
            RMC rmc = (RMC)datamap.get(d);
            newData.put(d, new ScalarValue(rmc.getSog()));
          }
          else if ("VWR".equals(data))
          {
            Wind wind = (Wind)datamap.get(d);
            newData.put(d, new ScalarValue(wind.speed));
          }
          else if ("VHW".equals(data))
          {
            NMEAAnalyzer.Bsp bsp = (NMEAAnalyzer.Bsp)datamap.get(d);
            newData.put(d, bsp);
          }
          else if ("VTG".equals(data))
          {
            OverGround og = (OverGround)datamap.get(d);
            newData.put(d, new ScalarValue(og.getSpeed()));
          }
          else if ("MTW".equals(data))
          {
            NMEAAnalyzer.WaterTemp wt = (NMEAAnalyzer.WaterTemp)datamap.get(d);
            newData.put(d, wt);
          }
          else if ("MTA".equals(data))
          {
            NMEAAnalyzer.AirTemp at = (NMEAAnalyzer.AirTemp)datamap.get(d);
            newData.put(d, at);
          }
          else if ("MMB".equals(data))
          {
            NMEAAnalyzer.AtmPressure ap = (NMEAAnalyzer.AtmPressure)datamap.get(d);
            newData.put(d, ap);
          }
        }        
      }
      if (laa[idx] == null)
      {
        try
        {
          GeoPoint gp = positionPanel.getPosition();
          laa[idx] = new LogAnalysis(newData, newTitle, newUnit, new GeoPos(gp.getL(), gp.getG()), (String)timeZoneComboBox.getSelectedItem());
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }
      if (laa[idx] != null)
      {
        if (b)
        {
          laa[idx].setTimeZone((String)timeZoneComboBox.getSelectedItem());
          laa[idx].show();
        }
        else
          laa[idx].hide();
      }
      else
        System.out.println("Ooops...");
    }
  }

  private void jbInit()
    throws Exception
  {
    loggedDataTable = new LoggedDataTable(this);
    this.setLayout(layout);
    setSize(new Dimension(450, 440));
    fileInLabel.setText("Log file Name:");
    fileInTextField.setPreferredSize(new Dimension(200, 19));
    browseFileInButton.setText("...");
    browseFileInButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        browseFileInButton_actionPerformed(e);
      }
    });
    parseButton.setText("Parse");
    parseButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        parseButton_actionPerformed(e);
      }
    });
    this.add(fileInLabel,
             new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(fileOutLabel,
             new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(fileInTextField,
             new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(browseFileInButton,
             new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(parseButton,
             new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                    new Insets(10, 0, 0, 0), 0, 0));
    this.add(loggedDataTable,
             new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(5, 0, 0, 0), 0, 0));
    this.add(positionPanel,
             new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(5, 0, 5, 0), 0, 0));
    radioButtonPanel.add(gpsRadioButton, null);
    radioButtonPanel.add(manualRadioButton, null);
    this.add(radioButtonPanel,
             new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(tzPanel,
             new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
    timeZoneComboBox.removeAllItems();
    String[] tzIDs = TimeZone.getAvailableIDs();
    for (String tz : tzIDs)
      timeZoneComboBox.addItem(tz);
    timeZoneComboBox.setSelectedItem("Etc/UTC");
    timeZoneComboBox.setPreferredSize(new Dimension(200, 20));
    tzPanel.add(timeZoneComboBox, null);
    tzPanel.add(narrowTextField, null);
    narrowTextField.setToolTipText("Restriction on the Time Zone (filter, regex)");
    narrowTextField.setPreferredSize(new Dimension(100, 20));
    narrowTextField.getDocument().addDocumentListener(new DocumentListener()
    {
      public void insertUpdate(DocumentEvent e)
      {
        narrowTZList(narrowTextField.getText());
      }

      public void removeUpdate(DocumentEvent e)
      {
        narrowTZList(narrowTextField.getText());
      }

      public void changedUpdate(DocumentEvent e)
      {
        narrowTZList(narrowTextField.getText());
      }
    });
    
    loggedDataTable.setPreferredSize(new Dimension(300, 150));
    group.add(gpsRadioButton);
    group.add(manualRadioButton);
    gpsRadioButton.setSelected(false);
    manualRadioButton.setSelected(true);
    gpsRadioButton.setText("From GPS");
    manualRadioButton.setText("Manual Entry");
    gpsRadioButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        gpsRadioButton_actionPerformed(e);
      }
    });
    manualRadioButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        manualPosRadioButton_actionPerformed(e);
      }
    });
    NMEAContext.getInstance().addNMEAReaderListener(new NMEAReaderListener()
    {
      public void manageNMEAString(String str)
      {
    //  System.out.println("NMEA:" + str);
        if (str.trim().length() > 6 && str.startsWith("$") && gpsRadioButton.isSelected())
        {
          if (StringParsers.validCheckSum(str) && (str.substring(3, 6).equals("RMC") ||
                                                   str.substring(3, 6).equals("GLL")))
          {
            try
            {
              if (NMEAContext.getInstance().getCache().get(NMEADataCache.POSITION) != null)
              {
                GeoPos position = (GeoPos)NMEAContext.getInstance().getCache().get(NMEADataCache.POSITION);
                positionPanel.setPosition(position.lat, position.lng);
              }
            }
            catch (Exception ex)
            {
              // No cache yet
            }
          }
        }
      }
    });
  }

  private void narrowTZList(String filter)
  {
    Pattern pattern = Pattern.compile(".*" + filter + ".*", Pattern.CASE_INSENSITIVE);
    timeZoneComboBox.removeAllItems();
    for (String tz : TimeZone.getAvailableIDs())
    {
      Matcher tzMatcher = pattern.matcher(tz);
      if (tzMatcher.find())
        timeZoneComboBox.addItem(tz);
    }
//  String tz = (String)timeZoneComboBox.getSelectedItem();
  }

  private void browseFileInButton_actionPerformed(ActionEvent e)
  {
    String fileIn = Utilities.chooseFile(JFileChooser.FILES_ONLY, "nmea", "NMEA Log-Files", "Choose the logged data file", "Select");
    if (fileIn.trim().length() > 0)
      fileInTextField.setText(fileIn);
  }

  private void manualPosRadioButton_actionPerformed(ActionEvent e)
  {
    positionPanel.setEnabled(manualRadioButton.isSelected());
    positionPanel.showButton(manualRadioButton.isSelected());
  }

  private void gpsRadioButton_actionPerformed(ActionEvent e)
  {
    positionPanel.setEnabled(manualRadioButton.isSelected());
    positionPanel.showButton(manualRadioButton.isSelected());
  }

  private void parseButton_actionPerformed(ActionEvent e)
  {
    String fIn  = fileInTextField.getText();
    parseButton.setEnabled(false);
    try
    {
      GeoPoint gp = positionPanel.getPosition();   
      this.na = new NMEAAnalyzer(fIn, new GeoPos(gp.getL(), gp.getG()));
      
      Map<String, Integer> map = na.getDataMap();
      // Dump
      for (String s : map.keySet())
        System.out.println(s + ":" + map.get(s).intValue() + ", " + (Constants.getInstance().getNMEAMap().get(s) == null ? "[Non standard]" : Constants.getInstance().getNMEAMap().get(s)));
      System.out.println("================================");
      
      Map<String, Map<Date, Object>> fullData = na.getFullData();
      System.out.println("Full Data: " + fullData.size() + " entry(ies).");
      
      Set<String> keys = fullData.keySet();
      for (String k : keys)
        loggedDataTable.addLineInTable(k);
      
      laa = new LogAnalysis[keys.size()];
      for (int i=0; i<laa.length; i++)
        laa[i] = null;
      
      loggedDataTable.repaint();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    parseButton.setEnabled(true);
  }
}