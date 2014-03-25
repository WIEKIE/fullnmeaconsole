package nmea.ui.viewer.spot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.Graphics2D;
import java.awt.Point;

import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.TimeZone;

import javax.swing.JPanel;

import nmea.event.NMEAReaderListener;

import nmea.server.ctx.NMEAContext;

import nmea.server.utils.Utils;

import nmea.ui.viewer.spot.utils.SpotParser.SpotLine;

import ocss.nmea.parser.GeoPos;

public class SpotCanvas
     extends JPanel
  implements MouseListener, MouseMotionListener
{
  private final static SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-YYYY HH:mm Z");
  static 
  {
    SDF.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
  }
  private boolean withDate = true, withRawData = true, withSmoothData = true;
  
  @Override
  public void mouseClicked(MouseEvent mouseEvent)
  {
    if (this.spotLines != null)
    {
      int x = mouseEvent.getX();
      double xScale = (double)this.getWidth() / (double)(this.spotLines.size() - 1);
      int i = (int)((double)x / xScale);
      NMEAContext.getInstance().fireSetSpotLineIndex(i);
    }
  }

  @Override
  public void mousePressed(MouseEvent mouseEvent)
  {
    // TODO Implement this method
  }

  @Override
  public void mouseReleased(MouseEvent mouseEvent)
  {
    // TODO Implement this method
  }

  @Override
  public void mouseEntered(MouseEvent mouseEvent)
  {
    // TODO Implement this method
  }

  @Override
  public void mouseExited(MouseEvent mouseEvent)
  {
    // TODO Implement this method
  }

  @Override
  public void mouseDragged(MouseEvent mouseEvent)
  {
    // TODO Implement this method
  }

  @Override
  public void mouseMoved(MouseEvent mouseEvent)
  {
    if (this.spotLines != null)
    {
      int x = mouseEvent.getX();
      double xScale = (double)this.getWidth() / (double)(this.spotLines.size() - 1);
      int i = (int)((double)x / xScale);
      NMEAContext.getInstance().fireSetSpotLineIndex(i);
      String tt = "<html>";
      tt += SDF.format(this.spotLines.get(i).getDate());
      tt += "<br>";
      tt += ("WIND:" + this.spotLines.get(i).getTws() + "kts @ " + this.spotLines.get(i).getTwd() + "\272");
      tt += "<br>";
      tt += ("PRMSL:" + this.spotLines.get(i).getPrmsl() + "hPa");
      tt += "<br>";
      tt += ("RAIN:" + this.spotLines.get(i).getRain() + "mm/h");
      tt += "</html>";
      this.setToolTipText(tt);
    }
  }
  
  private List<SpotLine> spotLines = null;
  
  public SpotCanvas()
  {
    jbInit();
  }

  private void jbInit()
  {
    this.setBackground(Color.lightGray);
    this.setPreferredSize(new Dimension(400, 300));
    NMEAContext.getInstance().addNMEAReaderListener(new NMEAReaderListener()
      {
        public void newSpotData(List<SpotLine> spotLines, GeoPos pos)
        {
          setSpotLines(spotLines);
        }
      });
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
  }
  
  public void setWithDate(boolean b)
  {
    this.withDate = b;
    this.repaint();
  }
  
  public void setWithRawData(boolean b)
  {
    this.withRawData = b;
    this.repaint();
  }
  
  public void setWithSmoothData(boolean b)
  {
    this.withSmoothData = b;
    this.repaint();
  }
  
  public void setTimeZone(String tzID)
  {
    if (tzID != null)
    {
      try { SDF.setTimeZone(TimeZone.getTimeZone(tzID)); }
      catch (NullPointerException npe)
      {
        System.out.println("NPE for [" + tzID + "]");
      }
      this.repaint();
    }
  }
  
  public void paintComponent(Graphics gr)
  {    
    Graphics2D g2d = (Graphics2D) gr;
    gr.setColor(Color.lightGray);
    gr.fillRect(0, 0, this.getWidth(), this.getHeight());
    gr.setColor(Color.darkGray);
    if (this.spotLines == null || this.spotLines.size() == 0)
    {
      // NO DATA
      String mess = "NO DATA";
      Font f = gr.getFont();
      gr.setFont(f.deriveFont(Font.ITALIC | Font.BOLD, 30f));
      int strWidth  = gr.getFontMetrics(gr.getFont()).stringWidth(mess);
      gr.drawString(mess, (this.getWidth() / 2) - (strWidth / 2), gr.getFont().getSize() + 2);
      gr.setFont(f);
    }
    else
    {
      // Calculate extrema
      Date fromDate = null;
      Date toDate   = null;
      double maxWind = 0D;
      for (SpotLine sp : this.spotLines)
      {
        Date date = sp.getDate();
        double tws = sp.getTws();
        maxWind = Math.max(maxWind, tws);
        if (fromDate == null)
          fromDate = date;
        toDate = date;
      }
//    System.out.println("MaxWind:" + maxWind);
      // Wind Speed
      double yScale = this.getHeight() / maxWind;
      double xScale = (double)this.getWidth() / (double)(this.spotLines.size() - 1);
      gr.setColor(Color.darkGray);
      for (int i=1; i<this.spotLines.size(); i++)
        gr.drawLine((int)(i * xScale), 0, (int)(i * xScale), this.getHeight());
      gr.setColor(Color.blue);
      Point prevPoint = null;
      for (int i=0; i<this.spotLines.size() && withRawData; i++)
      {
        Point currPoint = new Point((int)(i * xScale), this.getHeight() - (int)(this.spotLines.get(i).getTws() * yScale));
        if (prevPoint != null)
        {
          gr.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
        }
        prevPoint = currPoint;
      }
      Stroke origStroke = ((Graphics2D) gr).getStroke();
      if (withSmoothData)
      {
        gr.setColor(Color.red);
        Stroke stroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
        g2d.setStroke(stroke);
        List<Double> smoothedTWS = smoothTWSData();
        double smothedXScale = (double)this.getWidth() / (double)(smoothedTWS.size() - 1);
        prevPoint = null;
        for (int i=0; i<smoothedTWS.size(); i++)
        {
          Point currPoint = new Point((int)(i * smothedXScale), this.getHeight() - (int)(smoothedTWS.get(i).doubleValue() * yScale));
          if (prevPoint != null)
          {
            gr.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
          }
          prevPoint = currPoint;
        }
        g2d.setStroke(origStroke);
      }
      // Wind dir
      gr.setColor(Color.blue);
      origStroke = ((Graphics2D) gr).getStroke();
      Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
      g2d.setStroke(stroke);
      int radius = 20;
      for (int i=0; i<this.spotLines.size(); i++)
      {
        int xPos = (int)(i * xScale);
        double direction = this.spotLines.get(i).getTwd();
        int x = (xPos) + (int) ((radius) * Math.cos(Math.toRadians(direction - 90)));
        int y = (this.getHeight() / 2) + (int) ((radius) * Math.sin(Math.toRadians(direction - 90)));
        //  g.drawLine((dim.width / 2), (dim.height / 2), x, y);
        Utils.drawArrow((Graphics2D)gr, new Point(xPos, (this.getHeight() / 2)), new Point(x, y), Color.blue, 12);    
      }
      ((Graphics2D) gr).setStroke(origStroke);
      
      g2d.setColor(Color.black);
      for (int i=0; i<this.spotLines.size() && withDate; i++)
      {
        int xPos = (int)(i * xScale);
        g2d.rotate(Math.toRadians(-90), xPos, this.getHeight() - 2);
        g2d.drawString(SDF.format(this.spotLines.get(i).getDate()), xPos, (this.getHeight() - 2) + (gr.getFont().getSize() / 2));
        g2d.rotate(Math.toRadians(90), xPos, this.getHeight() - 2);
      }
    }
  }
  
  private List<Double> smoothTWSData()
  {
    List<Double> smoothData = new ArrayList<Double>();
    // 1 - More data (10 times more)
    for (int i=0; i<this.spotLines.size() - 1; i++)
    {
      for (int j=0; j<10; j++)
      {
        double _tws = this.spotLines.get(i).getTws() + (j * (this.spotLines.get(i + 1).getTws() - this.spotLines.get(i).getTws()) / 10);
        smoothData.add(_tws);
      }
    }
    // 2 - Smooth
    List<Double>  _smoothData = new ArrayList<Double>();
    int smoothWidth = 20;
    for (int i=0; i<smoothData.size(); i++)
    {
      double yAccu = 0;
      for (int acc=i-(smoothWidth / 2); acc<i+(smoothWidth/2); acc++)
      {
        double y;
        if (acc < 0)
          y = smoothData.get(0).doubleValue();
        else if (acc > (smoothData.size() - 1))
          y = smoothData.get(smoothData.size() - 1).doubleValue();
        else
          y = smoothData.get(acc).doubleValue();
        yAccu += y;
      }
      yAccu = yAccu / smoothWidth;
      _smoothData.add(yAccu);
//    console.log("I:" + smoothData[i].getX() + " y from " + smoothData[i].getY() + " becomes " + yAccu);
    }
    smoothData = _smoothData;
    return smoothData;
  }
  
  private void setSpotLines(List<SpotLine> spotLines)
  {
    this.spotLines = spotLines;
  }
}
