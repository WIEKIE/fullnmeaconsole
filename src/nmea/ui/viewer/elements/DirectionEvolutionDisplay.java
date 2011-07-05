package nmea.ui.viewer.elements;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import java.awt.Stroke;
import java.awt.event.MouseEvent;

import java.awt.event.MouseMotionListener;

import java.io.IOException;
import java.io.InputStream;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;

import nmea.ctx.NMEAContext;

import nmea.ctx.Utils;

import nmea.event.NMEAListener;

public class DirectionEvolutionDisplay
     extends JPanel
  implements MouseMotionListener
{
  private Font digiFont = null;
  private Color displayColor = Color.green;

  private String toolTipText = null;

  private String name = "HDG";
  private transient ArrayList<DatedData> aldd = null;
  private transient ArrayList<DatedData> alnddd = null;
  public final static int DEFAULT_WIDTH = 150;

  private long maxDataLength = NMEAContext.DEFAULT_BUFFER_SIZE;

  private double min = -180d, max = 180d;
  private double step = 45d;
  private String unit = "\272";
  private DecimalFormat df21 = new DecimalFormat("##0");

  private DataPanel    dataPanel    = new DataPanel();
  private HeadingPanel headingPanel = null;

  private boolean showRawData = true;
  
  public DirectionEvolutionDisplay(String name)
  {
    this.name = name;
    aldd = new ArrayList<DatedData>();
    alnddd = new ArrayList<DatedData>();
  }

  public DirectionEvolutionDisplay(String name, String ttText)
  {
    this(name, ttText, 36);
  }

  public DirectionEvolutionDisplay(String name, String ttText, int basicSize)
  {
    this(name, ttText, basicSize, HeadingPanel.ROSE);
  }
  
  public DirectionEvolutionDisplay(String name, String ttText, int basicSize, int compassType)
  {
    headingPanel = new HeadingPanel(compassType);
    this.name = name;
    toolTipText = ttText;
    this.jumboFontSize = basicSize;
    aldd = new ArrayList<DatedData>();
    alnddd = new ArrayList<DatedData>();
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private int jumboFontSize = 36;

  private void jbInit()
    throws Exception
  {
    try
    {
      digiFont = tryToLoadFont("ds-digi.ttf", this);
    }
    catch (Exception ex)
    {
      System.err.println(ex.getMessage());
    }
    if (digiFont == null)
      digiFont = new Font("Courier New", Font.BOLD, jumboFontSize);
    else
      digiFont = digiFont.deriveFont(Font.BOLD, jumboFontSize);
    digiFont = loadDigiFont();
    this.setLayout(new BorderLayout());
    this.setBackground(Color.black);

    //  resize(jumboFontSize);
    addMouseMotionListener(this);
    this.setSize(new Dimension(DEFAULT_WIDTH, 400));
    this.setPreferredSize(new Dimension(DEFAULT_WIDTH, 400));
    if (toolTipText != null)
      this.setToolTipText(toolTipText);

    this.add(dataPanel, BorderLayout.CENTER);
    JPanel compassHolder = new JPanel(new BorderLayout());
    compassHolder.add(headingPanel, BorderLayout.CENTER);
    compassHolder.add(new JPanel(), BorderLayout.EAST);
    compassHolder.add(new JPanel(), BorderLayout.WEST);
    this.add(compassHolder, BorderLayout.SOUTH);
    headingPanel.setDraggable(false);
    headingPanel.setRoseWidth(180f);
    headingPanel.setWithNumber(false);
    
    NMEAContext.getInstance().addNMEAListener(new NMEAListener()
    {
      public void dataBufferSizeChanged(int size) 
      {
        maxDataLength = size;
      }
      public void showRawData(boolean b) 
      {
        showRawData = b;
      }
    });
  }

  public void resize(int bigFontSize)
  {
    jumboFontSize = bigFontSize;
    //    int width = (int) (120d * (double) jumboFontSize / 36d);
    //    int height = (int) (65d * (double) jumboFontSize / 36d);
    //    this.setSize(new Dimension(width, height));
    //    this.setPreferredSize(new Dimension(width, height));
  }

  private Font loadDigiFont()
  {
    Font f = null;
    try
    {
      f = tryToLoadFont("ds-digi.ttf", this);
    }
    catch (Exception ex)
    {
      System.err.println(ex.getMessage());
    }
    if (f == null)
      f = new Font("Courier New", Font.BOLD, jumboFontSize);
    else
      f = f.deriveFont(Font.BOLD, jumboFontSize);
    return f;
  }

  public static Font tryToLoadFont(String fontName, Object parent)
  {
    final String RESOURCE_PATH = "resources" + "/"; // A slash! Not File.Separator, it is a URL.
    try
    {
      String fontRes = RESOURCE_PATH + fontName;
      InputStream fontDef = parent.getClass().getResourceAsStream(fontRes);
      if (fontDef == null)
      {
        throw new NullPointerException("Could not find font resource \"" + fontName + "\"\n\t\tin \"" + fontRes + "\"\n\t\tfor \"" + parent.getClass().getName() + "\"\n\t\ttry: " + parent.getClass().getResource(fontRes));
      }
      else
        return Font.createFont(Font.TRUETYPE_FONT, fontDef);
    }
    catch (FontFormatException e)
    {
      System.err.println("getting font " + fontName);
      e.printStackTrace();
    }
    catch (IOException e)
    {
      System.err.println("getting font " + fontName);
      e.printStackTrace();
    }
    return null;
  }

  public void setName(String s)
  {
    name = s;
  }

  public void addValue(Date date, double value)
  {
    synchronized (aldd)
    {
      if (!Double.isInfinite(value) && value != -Double.MAX_VALUE && (aldd.size() == 0 || (date.getTime() - aldd.get(aldd.size() - 1).getDate().getTime() > 1000L)))
        aldd.add(new DatedData(date, value));
      while (aldd.size() > maxDataLength)
        aldd.remove(0);
    }
  }

  public void addNDValue(Date date, double value)
  {
    synchronized (alnddd)
    {
      if (!Double.isInfinite(value) && value != -Double.MAX_VALUE && (alnddd.size() == 0 || (date.getTime() - alnddd.get(alnddd.size() - 1).getDate().getTime() > 1000L)))
        alnddd.add(new DatedData(date, value));
      while (alnddd.size() > maxDataLength)
        alnddd.remove(0);
    }
  }

  public void setDisplayColor(Color c)
  {
    displayColor = c;
  }

  public void paintComponent(Graphics g)
  {
    // super.paintComponent(g);
  }

  public void setMaxDataLength(long maxDataLength)
  {
    this.maxDataLength = maxDataLength;
  }

  public long getMaxDataLength()
  {
    return maxDataLength;
  }

  public void setMin(double min)
  {
    this.min = min;
  }

  public double getMin()
  {
    return min;
  }

  public void setMax(double max)
  {
    this.max = max;
  }

  public void setStep(double step)
  {
    this.step = step;
  }

  public void setUnit(String unit)
  {
    this.unit = unit;
  }

  public void setEditMask(DecimalFormat df21)
  {
    this.df21 = df21;
  }

  public void mouseDragged(MouseEvent e)
  {
  }

  public void mouseMoved(MouseEvent e)
  {
    Point mouse = e.getPoint();
    int height = dataPanel.getHeight();
    int arraySize = aldd.size();
    if (mouse.y <= height && arraySize > 0)
    {
      int index = (int)(((double)mouse.y / (double)height) * (double)arraySize);
      index -= 1;
      while (index >= aldd.size())
        index--;
      if (index == -1)
        return;
        
      try
      {
        Date date = aldd.get(index).getDate();
        String str = "<html>";
        str += (aldd.size() + " positions<br>");
        long range = aldd.get(aldd.size() - 1).getDate().getTime() - aldd.get(0).getDate().getTime();
        str += ("Range:" + Utils.setRange(range) + "<br>");
        str += ("From " + aldd.get(0).getDate().toString() + "<br>");
        str += ("To " + aldd.get(aldd.size() - 1).getDate().toString() + "<br>");
        str += ("index " + index + ":<br><b>" + date.toString() + "</b><br><font color='red'><b>" + df21.format(aldd.get(index).getValue()) + unit + "</b></font>");
        str += "</html>";
        this.setToolTipText(str);
      }
      catch (Exception ex)
      {
        System.err.println("(Managed Exception) " + ex.toString());        
      }
//    System.out.println("Array Size:" + aldd.size() + ", Pos:" + index);
    }
  }

  private class DatedData
  {
    private Date date;
    private double value;

    public DatedData(Date date, double d)
    {
      this.date = date;
      this.value = d;
    }

    public Date getDate()
    {
      return date;
    }

    public double getValue()
    {
      return value;
    }
  }

  private class DataPanel
    extends JPanel
  {
    public DataPanel()
    {
      super();
      //  this.setMinimumSize(new Dimension(400, 100));
      this.setPreferredSize(new Dimension(DEFAULT_WIDTH, 300));
    }

    public void paintComponent(Graphics gr)
    {
      Color startColor = Color.black; // new Color(255, 255, 255);
      Color endColor = Color.gray; // new Color(102, 102, 102);
      GradientPaint gradient = new GradientPaint(0, this.getHeight(), startColor, 0, 0, endColor); // vertical, upside down
      ((Graphics2D) gr).setPaint(gradient);
      gr.fillRect(0, 0, this.getWidth(), this.getHeight());

      gr.setColor(displayColor);
      int small = (int) (20d * jumboFontSize / 36d);
      gr.setFont(digiFont.deriveFont(Font.BOLD, small));
      gr.drawString(name, 10, small + 5);
      // Draw Data Curve
      synchronized (aldd)
      {
        // 1 - Length
        long begin = 0L;
        long end = 0L;
        double center = 0d;
        if (aldd != null && aldd.size() > 1)
        {
          begin = aldd.get(0).getDate().getTime();
          end = aldd.get(aldd.size() - 1).getDate().getTime();
          // Center value: last value
          center = aldd.get(aldd.size() - 1).getValue();
        }
        min = center - 180d;
        max = min + 360;
        // Grid and data
        long length = (end - begin);
        if (length > 0)
        {
          int w = this.getWidth();
          int h = this.getHeight();
          double stepH = ((double) w / (max - min));
          double stepV = ((double) h / (double) length);
          gr.setColor(new Color(0, 128, 64));
          // Grid
          double firstGridVal = min;
          double remainder = firstGridVal % step;
          firstGridVal -= remainder;
          while (firstGridVal < min) firstGridVal += step;
          for (double d = firstGridVal; d <= max; d += step)
          {
            int x = (int) ((d - min) * stepH);
            gr.drawLine(x, 0, x, h);
          }
          // No damped Data
          gr.setColor(Color.yellow);
          Point previous = null;
          
          if (showRawData)
          {
            for (DatedData dd: alnddd)
            {
              double value = dd.getValue();
            //          value += dataOffset;
              while (value > max) value -= 360d;
              while (value < min) value += 360d;
              
              int x = (int) ((value - min) * stepH);
              int y = (int) ((dd.getDate().getTime() - begin) * stepV);
              Point p = new Point(x, y);
              if (previous != null)
                gr.drawLine(previous.x, previous.y, p.x, p.y);
              previous = p;
            }
          }
          // Data
          Stroke origStroke = ((Graphics2D)gr).getStroke();
          Stroke stroke =  new BasicStroke(2, 
                                           BasicStroke.CAP_BUTT,
                                           BasicStroke.JOIN_BEVEL);
          ((Graphics2D)gr).setStroke(stroke);  
          gr.setColor(Color.red);
       /* Point */ previous = null;
          for (DatedData dd: aldd)
          {
            double value = dd.getValue();
//          value += dataOffset;
            while (value > max) value -= 360d;
            while (value < min) value += 360d;
            
            int x = (int) ((value - min) * stepH);
            int y = (int) ((dd.getDate().getTime() - begin) * stepV);
            Point p = new Point(x, y);
            if (previous != null)
              gr.drawLine(previous.x, previous.y, p.x, p.y);
            previous = p;
          }
          ((Graphics2D)gr).setStroke(origStroke);  
          // Last value
          String str = df21.format(aldd.get(aldd.size() - 1).getValue()); // + unit;
          int strWidth  = gr.getFontMetrics(gr.getFont()).stringWidth(str);
          gr.setColor(Color.yellow);
          gr.drawString(str, (this.getWidth() / 2) - (strWidth / 2),  this.getHeight() - 3);
          headingPanel.setHdg((int)Math.round(aldd.get(aldd.size() - 1).getValue()));
        }
        else
          gr.drawString("No Data", 10, 20);
      }
    }
  }
}