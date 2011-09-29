package nmea.ui.viewer.elements;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.FocusAdapter;

import java.awt.event.FocusEvent;

import java.text.DecimalFormat;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nmea.server.ctx.NMEAContext;
import nmea.server.ctx.NMEADataCache;

import nmea.ui.viewer.CurrentSituationPanel;

import ocss.nmea.parser.Angle180EW;


public class ControlPanelForAll
  extends JPanel
{
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private final static DecimalFormat DF22 = new DecimalFormat("00.00");
  private final static DecimalFormat DF24 = new DecimalFormat("00.0000");
  private final static DecimalFormat DF3  = new DecimalFormat("000");

  private CurrentSituationPanel parent = null;

  private JLabel jLabel2 = new JLabel();
  private JFormattedTextField bspCoeffTextField = new JFormattedTextField(DF24);
  private JLabel jLabel4 = new JLabel();
  private JFormattedTextField hdgOffsetTextField = new JFormattedTextField(DF3);
  private JLabel jLabel5 = new JLabel();
  private JFormattedTextField awsCoeffTextField = new JFormattedTextField(DF24);
  private JLabel jLabel6 = new JLabel();
  private JFormattedTextField awaOffsetTextField = new JFormattedTextField(DF3);
  private JLabel jLabel9 = new JLabel();
  private JFormattedTextField maxLeewayTextField = new JFormattedTextField();
  private JLabel jLabel1 = new JLabel();
  private JComboBox scaleComboBox = new JComboBox();
  
//private boolean frozen = false;
  
  private JLabel defaultDeclinationLabel = new JLabel();
  private JFormattedTextField defaultDeclinationFormattedTextField = new JFormattedTextField(DF22);
  private JLabel dampingLabel = new JLabel();
  private JSpinner dampingSpinner = new JSpinner();
  private JSlider replaySpeedSlider = new JSlider();

  public ControlPanelForAll(CurrentSituationPanel cp)
  {
    try
    {
      this.parent = cp;
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
    this.setLayout(gridBagLayout1);

    this.setSize(new Dimension(485, 123));
    jLabel2.setText("BSP Coeff:");
    bspCoeffTextField.setPreferredSize(new Dimension(40, 20));
    bspCoeffTextField.setHorizontalAlignment(JTextField.TRAILING);
    double bspCoeff = 1d;
    try { bspCoeff = ((Double) NMEAContext.getInstance().getDataCache(NMEADataCache.BSP_FACTOR)).doubleValue(); } catch (Exception ex) {}
    bspCoeffTextField.setText(DF24.format(bspCoeff));
    bspCoeffTextField.setMinimumSize(new Dimension(35, 20));
    bspCoeffTextField.setSize(new Dimension(40, 20));
    bspCoeffTextField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          bspCoeffTextField_actionPerformed(e);
        }
      });
    bspCoeffTextField.addFocusListener(new FocusAdapter()
      {
        public void focusLost(FocusEvent e)
        {
          bspCoeffTextField_focusLost(e);
        }
      });
    jLabel4.setText("HDG Offset:");
    hdgOffsetTextField.setPreferredSize(new Dimension(40, 20));
    hdgOffsetTextField.setHorizontalAlignment(JTextField.TRAILING);
    hdgOffsetTextField.setToolTipText("in degrees");
    double hdgOffset = 0d;
    try { hdgOffset = ((Double) NMEAContext.getInstance().getDataCache(NMEADataCache.HDG_OFFSET)).doubleValue(); } catch (Exception ex) {}
    hdgOffsetTextField.setText(DF3.format(hdgOffset));
    hdgOffsetTextField.setMinimumSize(new Dimension(35, 20));
    hdgOffsetTextField.setSize(new Dimension(40, 20));
    hdgOffsetTextField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          hdgOffsetTextField_actionPerformed(e);
        }
      });
    hdgOffsetTextField.addFocusListener(new FocusAdapter()
      {
        public void focusLost(FocusEvent e)
        {
          hdgOffsetTextField_focusLost(e);
        }
      });
    jLabel5.setText("AWS Coeff:");
    awsCoeffTextField.setPreferredSize(new Dimension(40, 20));
    double awsCoeff = 1d;
    try { awsCoeff = ((Double) NMEAContext.getInstance().getDataCache(NMEADataCache.AWS_FACTOR)).doubleValue(); } catch (Exception ex) {}
    awsCoeffTextField.setText(DF22.format(awsCoeff));
    awsCoeffTextField.setHorizontalAlignment(JTextField.RIGHT);
    awsCoeffTextField.setMinimumSize(new Dimension(35, 20));
    awsCoeffTextField.setSize(new Dimension(40, 20));
    awsCoeffTextField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          awsCoeffTextField_actionPerformed(e);
        }
      });
    awsCoeffTextField.addFocusListener(new FocusAdapter()
      {
        public void focusLost(FocusEvent e)
        {
          awsCoeffTextField_focusLost(e);
        }
      });
    jLabel6.setText("AWA Offset:");
    awaOffsetTextField.setPreferredSize(new Dimension(40, 20));
    double awaOffset = 0d;
    try { awaOffset = ((Double) NMEAContext.getInstance().getDataCache(NMEADataCache.AWA_OFFSET)).doubleValue(); } catch (Exception ex) {}
    awaOffsetTextField.setText(DF3.format(awaOffset));
    awaOffsetTextField.setHorizontalAlignment(JTextField.RIGHT);
    awaOffsetTextField.setToolTipText("in degrees");
    awaOffsetTextField.setMinimumSize(new Dimension(35, 20));
    awaOffsetTextField.setSize(new Dimension(40, 20));
    awaOffsetTextField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          awaOffsetTextField_actionPerformed(e);
        }
      });
    awaOffsetTextField.addFocusListener(new FocusAdapter()
      {
        public void focusLost(FocusEvent e)
        {
          awaOffsetTextField_focusLost(e);
        }
      });
    jLabel9.setText("Max Leeway:");
    maxLeewayTextField.setPreferredSize(new Dimension(40, 20));
    double mlw = 0d;
    try { mlw = ((Double) NMEAContext.getInstance().getDataCache(NMEADataCache.MAX_LEEWAY)).doubleValue(); } catch (Exception ex) {}
    maxLeewayTextField.setText(DF22.format(mlw));
    maxLeewayTextField.setHorizontalAlignment(JTextField.RIGHT);
    maxLeewayTextField.setToolTipText("in degrees");
    maxLeewayTextField.setMinimumSize(new Dimension(35, 20));
    maxLeewayTextField.setSize(new Dimension(40, 20));
    maxLeewayTextField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          maxLeewayTextField_actionPerformed(e);
        }
      });
    maxLeewayTextField.addFocusListener(new FocusAdapter()
      {
        public void focusLost(FocusEvent e)
        {
          maxLeewayTextField_focusLost(e);
        }
      });
    jLabel1.setText("Speed Scale:");
    scaleComboBox.removeAllItems();
    scaleComboBox.addItem(new ScaleForWind( 1.5f, "05 knots"));
    scaleComboBox.addItem(new ScaleForWind( 3,    "10 knots"));
    scaleComboBox.addItem(new ScaleForWind( 4.5f, "15 knots"));
    scaleComboBox.addItem(new ScaleForWind( 6,    "20 knots"));
    scaleComboBox.addItem(new ScaleForWind( 7.5f, "25 knots"));
    scaleComboBox.addItem(new ScaleForWind( 9,    "30 knots"));
    scaleComboBox.addItem(new ScaleForWind(12,    "40 knots"));
    scaleComboBox.addItem(new ScaleForWind(15,    "50 knots"));
    scaleComboBox.addItem(new ScaleForWind(18,    "60 knots"));

    float f = Float.parseFloat(System.getProperty("wind.scale", "-1"));
    for (int i=0; i<scaleComboBox.getItemCount(); i++)
    {
      ScaleForWind sfw = (ScaleForWind)scaleComboBox.getItemAt(i);
      if (sfw.getScale() == f)
      {
        scaleComboBox.setSelectedIndex(i);
        NMEAContext.getInstance().fireWindScale(f);
        System.setProperty("wind.scale", Float.toString(f));
        break;
      }
    }
    scaleComboBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          scaleComboBox_actionPerformed(e);
        }
      });

    defaultDeclinationLabel.setText("Default Declination:");
    defaultDeclinationFormattedTextField.setHorizontalAlignment(JTextField.RIGHT);
    defaultDeclinationFormattedTextField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          defaultDeclinationFormattedTextField.repaint();
          defaultDeclination_Changed(e);
        }
      });
    double dd = 0d;
    try { dd = ((Angle180EW) NMEAContext.getInstance().getDataCache(NMEADataCache.DEFAULT_DECLINATION)).getValue(); } catch (Exception ex) {}    
    defaultDeclinationFormattedTextField.setText(DF22.format(dd));

    defaultDeclinationFormattedTextField.setMinimumSize(new Dimension(35, 20));
    defaultDeclinationFormattedTextField.setPreferredSize(new Dimension(40, 20));
    defaultDeclinationFormattedTextField.setSize(new Dimension(40, 20));
    defaultDeclinationFormattedTextField.addFocusListener(new FocusAdapter()
      {
        public void focusLost(FocusEvent e)
        {
          defaultDeclinationFormattedTextField_focusLost(e);
        }
      });
    dampingLabel.setText("Damping:");    
    int dv = 1;
    try { dv = ((Integer) NMEAContext.getInstance().getDataCache(NMEADataCache.DAMPING)).intValue(); } catch (Exception ex) {}    
    dampingSpinner.setValue(new Integer(dv));
    dampingSpinner.setMinimumSize(new Dimension(35, 20));
    dampingSpinner.setPreferredSize(new Dimension(40, 20));
    dampingSpinner.setSize(new Dimension(40, 20));
    dampingSpinner.setToolTipText("In number of points");
    NMEAContext.getInstance().getCache().setDampingSize(dv);
    dampingSpinner.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent evt) 
      {
        JSpinner spinner = (JSpinner)evt.getSource();
        // Get the new value
        Object value = spinner.getValue();
        System.out.println("DampingValue changed: Value is a " + value.getClass().getName());
        if (value instanceof Integer)
        {
          Integer i = (Integer)value;
          int val = i.intValue();
          if (val < 1)
            val = 1;
          NMEAContext.getInstance().getCache().setDampingSize(val);
          NMEAContext.getInstance().getCache().put(NMEADataCache.DAMPING, val);
        }
      }
    });

    this.add(jLabel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.add(bspCoeffTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel4, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    this.add(hdgOffsetTextField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel5, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.add(awsCoeffTextField, new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel6, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    this.add(awaOffsetTextField, new GridBagConstraints(7, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel9, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
    this.add(maxLeewayTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
    this.add(jLabel1, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
    this.add(scaleComboBox, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));

    this.add(defaultDeclinationLabel, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 3, 0, 0), 0, 0));
    this.add(defaultDeclinationFormattedTextField, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 0, 0), 0, 0));
    this.add(dampingLabel, new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
    this.add(dampingSpinner, new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));

    this.add(replaySpeedSlider, new GridBagConstraints(0, 3, 8, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
    replaySpeedSlider.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent evt)
        {
          JSlider slider = (JSlider) evt.getSource();

          //    if (!slider.getValueIsAdjusting())
          {
            // Get new value
            int value = slider.getValue();
            NMEAContext.getInstance().fireReplaySpeedChanged(value);
          }
        }
      });
    replaySpeedSlider.setToolTipText("Replay Speed");
    replaySpeedSlider.setVisible(NMEAContext.getInstance().isFromFile());
  }

  private void bspCoeffTextField_actionPerformed(ActionEvent e)
  {
    bspCoefChanged();
  }  
  private void bspCoeffTextField_focusLost(FocusEvent e)
  {
    bspCoefChanged();
  }
  private void bspCoefChanged()
  {
    System.out.println("BSP Coeff changed");
    double d = Double.parseDouble(bspCoeffTextField.getText());
    NMEAContext.getInstance().putDataCache(NMEADataCache.BSP_FACTOR, d);
    if (NMEAContext.getInstance().getFrozenDataCache() != null)
      NMEAContext.getInstance().getFrozenDataCache().put(NMEADataCache.BSP_FACTOR, d);
    NMEAContext.getInstance().fireDataChanged();
    //  repaint();
  }

  private void hdgOffsetTextField_actionPerformed(ActionEvent e)
  {
    hdgOffsetChanged();
  }
  private void hdgOffsetTextField_focusLost(FocusEvent e)
  {
    hdgOffsetChanged();
  }
  private void hdgOffsetChanged()
  {
    System.out.println("HDG Offset changed");
    double d = Double.parseDouble(hdgOffsetTextField.getText());
    NMEAContext.getInstance().putDataCache(NMEADataCache.HDG_OFFSET, d);
    if (NMEAContext.getInstance().getFrozenDataCache() != null)
      NMEAContext.getInstance().getFrozenDataCache().put(NMEADataCache.HDG_OFFSET, d);
    NMEAContext.getInstance().fireDataChanged();
    //  repaint();
  }

  private void awsCoeffTextField_actionPerformed(ActionEvent e)
  {
    awsCoeffChanged();
  }
  private void awsCoeffTextField_focusLost(FocusEvent e)
  {
    awsCoeffChanged();
  }
  private void awsCoeffChanged()
  {
    System.out.println("AWS Coeff changed");
    double d = Double.parseDouble(awsCoeffTextField.getText());
    NMEAContext.getInstance().putDataCache(NMEADataCache.AWS_FACTOR, d);
    if (NMEAContext.getInstance().getFrozenDataCache() != null)
      NMEAContext.getInstance().getFrozenDataCache().put(NMEADataCache.AWS_FACTOR, d);
    NMEAContext.getInstance().fireDataChanged();
    //  repaint();
  }

  private void awaOffsetTextField_actionPerformed(ActionEvent e)
  {
    awaOffsetChanged();
  }
  private void awaOffsetTextField_focusLost(FocusEvent e)
  {
    awaOffsetChanged();
  }
  private void awaOffsetChanged()
  {
    System.out.println("AWA Offset changed");
    double d = Double.parseDouble(awaOffsetTextField.getText());
    NMEAContext.getInstance().putDataCache(NMEADataCache.AWA_OFFSET, d);
    if (NMEAContext.getInstance().getFrozenDataCache() != null)
      NMEAContext.getInstance().getFrozenDataCache().put(NMEADataCache.AWA_OFFSET, d);
    NMEAContext.getInstance().fireDataChanged();
    //  repaint();
  }

  private void maxLeewayTextField_actionPerformed(ActionEvent e)
  {
    maxLeewayChanged();
  }
  private void maxLeewayTextField_focusLost(FocusEvent e)
  {
    maxLeewayChanged();
  }
  private void maxLeewayChanged()
  {
    System.out.println("Max Leeway changed");
    double d = Double.parseDouble(maxLeewayTextField.getText());
    NMEAContext.getInstance().putDataCache(NMEADataCache.MAX_LEEWAY, d);
    if (NMEAContext.getInstance().getFrozenDataCache() != null)
      NMEAContext.getInstance().getFrozenDataCache().put(NMEADataCache.MAX_LEEWAY, d);
    NMEAContext.getInstance().fireDataChanged();
    //  repaint();
  }

  private void defaultDeclination_Changed(ActionEvent e)
  {
    ddChanged();
  }
  private void defaultDeclinationFormattedTextField_focusLost(FocusEvent e)
  {
    ddChanged();
  }
  private void ddChanged()
  {
    System.out.println("Default Declination changed");
    double d = Double.parseDouble(defaultDeclinationFormattedTextField.getText());
    NMEAContext.getInstance().putDataCache(NMEADataCache.DEFAULT_DECLINATION, new Angle180EW(d));
    if (NMEAContext.getInstance().getFrozenDataCache() != null)
      NMEAContext.getInstance().getFrozenDataCache().put(NMEADataCache.DEFAULT_DECLINATION, new Angle180EW(d));
    NMEAContext.getInstance().fireDataChanged();
//  repaint();
  }

  private void scaleComboBox_actionPerformed(ActionEvent e)
  {
    float f = ((ScaleForWind)scaleComboBox.getSelectedItem()).getScale();
//  parent.setWindScale(f);
    NMEAContext.getInstance().fireWindScale(f);
    System.setProperty("wind.scale", Float.toString(f));
  }

  class ScaleForWind
  {
    private float scale;
    private String label;
    public ScaleForWind(float scale, String label)
    {
      this.scale = scale;
      this.label = label;
    }
    
    public float getScale() { return scale; }
    public String toString() { return label; }
  }
}