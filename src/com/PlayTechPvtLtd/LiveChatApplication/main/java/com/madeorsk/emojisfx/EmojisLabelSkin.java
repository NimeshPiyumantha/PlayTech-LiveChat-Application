package com.PlayTechPvtLtd.LiveChatApplication.main.java.com.madeorsk.emojisfx;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class EmojisLabelSkin extends BehaviorSkinBase<EmojisLabel, EmojisLabelBehavior>
{
  private List<byte[]> charList = new ArrayList<byte[]>();
  private Range selectedRange;

  private StackPane main;
  private VBox lines;

  protected EmojisLabelSkin(EmojisLabel control)
  {
    super(control, new EmojisLabelBehavior(control));
    this.getBehavior().setSkin(this);

    if (this.getSkinnable().getText() != null)
      this.updateCharList(this.getSkinnable().getText());
    this.getSkinnable().textProperty().addListener(new ChangeListener<String>()
    {
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
      {
        EmojisLabelSkin.this.updateCharList(newValue);
      }
    });

    this.main = new StackPane();
    this.main.setAlignment(Pos.TOP_LEFT);
    this.getChildren().add(this.main);

    this.lines = new VBox();
    this.lines.setFillWidth(false);
    this.lines.heightProperty().addListener(new ChangeListener<Number>()
    {
      public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2)
      {
        Platform.runLater(new Runnable()
        {
          public void run()
          {
            EmojisLabelSkin.this.getSkinnable().requestLayout(); // For StackPane height update
          }
        });
      }
    });
    this.main.getChildren().add(this.lines);
  }

  public String getSelectedString()
  {
    return this.getBehavior().getSelectedString();
  }

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight)
  {
    super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
    try
    {
      this.updateViewText(this.chooseWidth(this.getSkinnable().getMinWidth(),
          this.getSkinnable().getPrefWidth(), this.getSkinnable().getMaxWidth(), contentWidth, this.getSkinnable().getInsets()));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private double chooseWidth(double minWidth, double prefWidth, double maxWidth, double contentWidth, Insets insets)
  {
    if (prefWidth < 0)
    {
      if (contentWidth < minWidth)
        return minWidth - insets.getLeft() - insets.getRight();
      else if (contentWidth > maxWidth && maxWidth >= 0)
        return maxWidth - insets.getLeft() - insets.getRight();
      else
        return contentWidth - insets.getLeft() - insets.getRight();
    }
    else
    {
      if (prefWidth < minWidth)
        return minWidth - insets.getLeft() - insets.getRight();
      else if (prefWidth > maxWidth && maxWidth >= 0)
        return maxWidth - insets.getLeft() - insets.getRight();
      else
        return prefWidth - insets.getLeft() - insets.getRight();
    }
  }

  public HBox getLine(int index)
  {
    return (HBox) this.lines.getChildren().get(index);
  }

  public Node getNode(int lineIndex, int nodeIndex)
  {
    try
    {
      return this.getLine(lineIndex).getChildren().get(nodeIndex);
    }
    catch (Exception e)
    {
      return null;
    }
  }

  public void selectAll()
  {
    int currentLineNumber = this.lines.getChildren().size() - 1;
    HBox currentLine = (HBox) this.lines.getChildren().get(currentLineNumber);
    int currentNodeNumber = currentLine.getChildren().size() - 1;
    this.evaluateSelection(((HBox) this.lines.getChildren().get(0)).getChildren().get(0), 0, (HBox) this.lines.getChildren().get(0), 0, currentLine.getChildren().get(currentNodeNumber), currentNodeNumber, currentLine, currentLineNumber, 0);
  }

  public void evaluateSelection(Node originNode, int originNodeNumber, HBox originLine, int originLineNumber, Node currentNode, int currentNodeNumber, HBox currentLine, int currentLineNumber, int clickCount)
  {
    this.clearSelectionGraphics();
    if (originLine.equals(currentLine) && clickCount != 3)
    {
      if (originNodeNumber <= currentNodeNumber)
      {
        if (clickCount == 2)
        {
          originNodeNumber = this.findWordOrigin(originLineNumber, originNodeNumber);
          if (originNodeNumber >= originLine.getChildren().size())
            originNodeNumber = originLine.getChildren().size() - 1;
          originNode = originLine.getChildren().get(originNodeNumber);

          currentNodeNumber = this.findWordEnd(currentLineNumber, currentNodeNumber);
          currentNode = currentLine.getChildren().get(currentNodeNumber);
        }
      }
      else
      {
        if (clickCount == 2)
        {
          originNodeNumber = this.findWordEnd(originLineNumber, originNodeNumber);
          originNode = originLine.getChildren().get(originNodeNumber);

          currentNodeNumber = this.findWordOrigin(currentLineNumber, currentNodeNumber);
          if (currentNodeNumber >= currentLine.getChildren().size())
            currentNodeNumber = currentLine.getChildren().size() - 1;
          currentNode = currentLine.getChildren().get(currentNodeNumber);
        }
      }

      Rectangle selectionRect = new Rectangle();
      selectionRect.setFill(this.getSkinnable().getSelectionFill());
      if (currentNode.getBoundsInParent().getMinX() < originNode.getBoundsInParent().getMinX())
      {
        selectionRect.setTranslateX(currentNode.getBoundsInParent().getMinX());
        selectionRect.setWidth(originNode.getBoundsInParent().getMaxX() - currentNode.getBoundsInParent().getMinX());
      }
      else
      {
        selectionRect.setTranslateX(originNode.getBoundsInParent().getMinX());
        selectionRect.setWidth(currentNode.getBoundsInParent().getMaxX() - originNode.getBoundsInParent().getMinX());
      }
      selectionRect.setTranslateY(originLine.getBoundsInParent().getMinY());
      selectionRect.setHeight(originLine.getHeight());

      String currentSelectedString = "";
      for (int i = Math.min(originNodeNumber, currentNodeNumber); i <= Math.max(originNodeNumber, currentNodeNumber) && i < originLine.getChildren().size(); i++)
      {
        Node n = originLine.getChildren().get(i);
        currentSelectedString += (n instanceof Text) ? ((Text) n).getText() : ((n instanceof EmojiView) ? n.toString() : "");
      }
      this.getBehavior().setSelectedString(currentSelectedString);

      this.main.getChildren().add(0, selectionRect);
    }
    else
    {
      String start = "";
      String middle = "";
      String end = "";
      if (originLineNumber < currentLineNumber || (originLineNumber == currentLineNumber && originNodeNumber < currentNodeNumber))
      {
        if (clickCount == 2)
        {
          originNodeNumber = this.findWordOrigin(originLineNumber, originNodeNumber);
          originNode = originLine.getChildren().get(originNodeNumber);

          currentNodeNumber = this.findWordEnd(currentLineNumber, currentNodeNumber);
          currentNode = currentLine.getChildren().get(currentNodeNumber);
        }
        else if (clickCount == 3)
        {
          int[] res = this.findParagraphOrigin(originLineNumber, originNodeNumber);
          originLineNumber = res[0];
          originLine = this.getLine(originLineNumber);
          if (res[1] >= 0)
            originNodeNumber = res[1];
          else
            originNodeNumber = originLine.getChildren().size() - 1;
          originNode = originLine.getChildren().get(originNodeNumber);

          res = this.findParagraphEnd(currentLineNumber, currentNodeNumber);
          currentLineNumber = res[0];
          currentLine = this.getLine(currentLineNumber);
          if (res[1] >= 0)
            currentNodeNumber = res[1];
          else
            currentNodeNumber = currentLine.getChildren().size() - 1;
          currentNode = currentLine.getChildren().get(currentNodeNumber);
        }

        Rectangle selectionRect = new Rectangle();
        selectionRect.setFill(this.getSkinnable().getSelectionFill());
        selectionRect.setTranslateX(originNode.getBoundsInParent().getMinX());
        selectionRect.setTranslateY(originLine.getBoundsInParent().getMinY());
        selectionRect.setHeight(originLine.getHeight());
        selectionRect.setWidth(originLine.getWidth() - originNode.getBoundsInParent().getMinX());
        this.main.getChildren().add(0, selectionRect);

        selectionRect = new Rectangle();
        selectionRect.setFill(this.getSkinnable().getSelectionFill());
        selectionRect.setTranslateX(0);
        selectionRect.setTranslateY(currentLine.getBoundsInParent().getMinY());
        selectionRect.setHeight(currentLine.getHeight());
        selectionRect.setWidth(currentNode.getBoundsInParent().getMaxX());
        this.main.getChildren().add(0, selectionRect);

        for (int i = originNodeNumber; i < originLine.getChildren().size(); i++)
        {
          Node n = originLine.getChildren().get(i);
          start += (n instanceof Text) ? ((Text) n).getText() : ((n instanceof EmojiView) ? n.toString() : "");
        }
        for (int i = 0; i <= currentNodeNumber && i < currentLine.getChildren().size(); i++)
        {
          Node n = currentLine.getChildren().get(i);
          end += (n instanceof Text) ? ((Text) n).getText() : ((n instanceof EmojiView) ? n.toString() : "");
        }
      }
      else
      {
        if (clickCount == 2)
        {
          originNodeNumber = this.findWordEnd(originLineNumber, originNodeNumber);
          originNode = originLine.getChildren().get(originNodeNumber);

          currentNodeNumber = this.findWordOrigin(currentLineNumber, currentNodeNumber);
          currentNode = currentLine.getChildren().get(currentNodeNumber);
        }
        else if (clickCount == 3)
        {
          int[] res = this.findParagraphEnd(originLineNumber, originNodeNumber);
          originLineNumber = res[0];
          originLine = this.getLine(originLineNumber);
          if (res[1] >= 0)
            originNodeNumber = res[1];
          else
            originNodeNumber = originLine.getChildren().size() - 1;
          originNode = originLine.getChildren().get(originNodeNumber);

          res = this.findParagraphOrigin(currentLineNumber, currentNodeNumber);
          currentLineNumber = res[0];
          currentLine = this.getLine(currentLineNumber);
          if (res[1] >= 0)
            currentNodeNumber = res[1];
          else
            currentNodeNumber = currentLine.getChildren().size() - 1;
          currentNode = currentLine.getChildren().get(currentNodeNumber);
        }

        Rectangle selectionRect = new Rectangle();
        selectionRect.setFill(this.getSkinnable().getSelectionFill());
        selectionRect.setTranslateX(currentNode.getBoundsInParent().getMinX());
        selectionRect.setTranslateY(currentLine.getBoundsInParent().getMinY());
        selectionRect.setHeight(currentLine.getHeight());
        selectionRect.setWidth(currentLine.getWidth() - currentNode.getBoundsInParent().getMinX());
        this.main.getChildren().add(0, selectionRect);

        selectionRect = new Rectangle();
        selectionRect.setFill(this.getSkinnable().getSelectionFill());
        selectionRect.setTranslateX(0);
        selectionRect.setTranslateY(originLine.getBoundsInParent().getMinY());
        selectionRect.setHeight(originLine.getHeight());
        selectionRect.setWidth(originNode.getBoundsInParent().getMaxX());
        this.main.getChildren().add(0, selectionRect);

        for (int i = currentNodeNumber; i < currentLine.getChildren().size(); i++)
        {
          Node n = currentLine.getChildren().get(i);
          start += (n instanceof Text) ? ((Text) n).getText() : ((n instanceof EmojiView) ? n.toString() : "");
        }
        for (int i = 0; i <= originNodeNumber && i < originLine.getChildren().size(); i++)
        {
          Node n = originLine.getChildren().get(i);
          end += (n instanceof Text) ? ((Text) n).getText() : ((n instanceof EmojiView) ? n.toString() : "");
        }
      }

      double max = Math.max(originLineNumber, currentLineNumber);
      for (int i = Math.min(originLineNumber, currentLineNumber) + 1; i < max && i < this.lines.getChildren().size(); i++)
      {
        HBox currentForLine = this.getLine(i);
        for (Node n : currentForLine.getChildren())
          middle += (n instanceof Text) ? ((Text) n).getText() : ((n instanceof EmojiView) ? n.toString() : "");

        Rectangle selectionRect = new Rectangle();
        selectionRect.setFill(this.getSkinnable().getSelectionFill());
        selectionRect.setTranslateX(0);
        selectionRect.setTranslateY(currentForLine.getBoundsInParent().getMinY());
        selectionRect.setHeight(currentForLine.getBoundsInParent().getHeight());
        selectionRect.setWidth(currentForLine.getBoundsInParent().getWidth());
        this.main.getChildren().add(0, selectionRect);
      }
      if (currentLine.equals(originLine))
        this.getBehavior().setSelectedString(start);
      else
        this.getBehavior().setSelectedString(start + middle + end);
    }
    this.selectedRange = new Range(this.findIdForRange(originLineNumber, originNodeNumber), this.findIdForRange(currentLineNumber, currentNodeNumber));
    this.getBehavior().lineNumberOnLastUpdate = currentLineNumber;
    this.getBehavior().nodeNumberOnLastUpdate = currentNodeNumber;
  }

  private int findIdForRange(int lineNumber, int nodeNumber)
  {
    int elementsCounter = 0;
    for (int l = 0; l < this.lines.getChildren().size(); l++)
    {
      HBox currentLine = (HBox) this.lines.getChildren().get(l);
      if (l == lineNumber)
      {
        for (int n = 0; n < currentLine.getChildren().size(); n++)
        {
          if (n == nodeNumber)
            break;
          else
            elementsCounter++;
        }
        return elementsCounter;
      }
      else
        elementsCounter += currentLine.getChildren().size();
    }
    return elementsCounter;
  }

  public int findLineNumberFromY(double y)
  {
    if (y < 0)
      return 0;
    if (y > this.lines.getHeight())
      return this.lines.getChildren().size() - 1;
    for (int i = 0; i < this.lines.getChildren().size(); i++)
    {
      Node n = this.lines.getChildren().get(i);
      if (n.getBoundsInParent().getMinY() + this.getSkinnable().getInsets().getTop() <= y && y <= n.getBoundsInParent().getMaxY() + this.getSkinnable().getInsets().getBottom())
        return i;
    }
    return -1;
  }

  public int findNodeNumberFromX(HBox line, double x)
  {
    if (x < 0)
      return 0;
    if (x > line.getWidth())
      return line.getChildren().size() - 1;
    for (int i = 0; i < line.getChildren().size(); i++)
    {
      Node n = line.getChildren().get(i);
      if (n.getBoundsInParent().getMinX() + this.getSkinnable().getInsets().getLeft() <= x && x <= n.getBoundsInParent().getMaxX() + this.getSkinnable().getInsets().getLeft())
        return i;
    }
    return -1;
  }

  private int findWordOrigin(int originLineNumber, int originNodeNumber)
  {
    for (int i = originNodeNumber; i >= 0; i--)
    {
      Node node = ((HBox) this.lines.getChildren().get(originLineNumber)).getChildren().get(i);
      if (node instanceof Text)
      {
        String textChar = ((Text) node).getText();
        if (textChar.equals(" ") || textChar.equals(".") || textChar.equals("/"))
          return i + 1;
      }
      else if (node instanceof EmojiView)
        return i;
    }
    return 0;
  }

  private int findWordEnd(int currentLineNumber, int currentNodeNumber)
  {
    int endOfLine = ((HBox) this.lines.getChildren().get(currentLineNumber)).getChildren().size();
    for (int i = currentNodeNumber; i < endOfLine; i++)
    {
      Node node = ((HBox) this.lines.getChildren().get(currentLineNumber)).getChildren().get(i);
      if (node instanceof Text)
      {
        String textChar = ((Text) node).getText();
        if (textChar.equals(" ") || textChar.equals(".") || textChar.equals("/"))
          return i - 1;
      }
      else if (node instanceof EmojiView)
        return i;
    }
    return endOfLine - 1;
  }

  private int[] findParagraphOrigin(int originLineNumber, int originNodeNumber)
  {
    for (int i = originLineNumber - 1; i >= 0; i--)
    {
      HBox line = (HBox) this.lines.getChildren().get(i);
      if (!line.getChildren().isEmpty())
        if (line.getChildren().get(line.getChildren().size() - 1) instanceof Text)
          if (((Text) line.getChildren().get(line.getChildren().size() - 1)).getText().equals("\n"))
            return new int[]{i + 1, 0};
    }
    return new int[]{0, 0};
  }

  private int[] findParagraphEnd(int currentLineNumber, int currentNodeNumber)
  {
    for (int i = currentLineNumber; i < this.lines.getChildren().size(); i++)
    {
      HBox line = (HBox) this.lines.getChildren().get(i);
      if (!line.getChildren().isEmpty())
        if (line.getChildren().get(line.getChildren().size() - 1) instanceof Text)
          if (((Text) line.getChildren().get(line.getChildren().size() - 1)).getText().equals("\n"))
            return new int[]{i, ((HBox) this.lines.getChildren().get(i)).getChildren().size() - 2};
    }
    return new int[]{this.lines.getChildren().size() - 1,
        ((HBox) this.lines.getChildren().get(this.lines.getChildren().size() - 1)).getChildren().size() - 1};
  }

  public void clearSelectionGraphics()
  {
    this.selectedRange = null;
    this.main.getChildren().clear();
    this.main.getChildren().add(this.lines);
  }

  private HBox createNewLine()
  {
    HBox line = new HBox();
    line.setAlignment(Pos.CENTER_LEFT);
    this.lines.getChildren().add(line);
    return line;
  }

  private void updateViewText(double contentWidth) throws MalformedURLException, IOException
  {
    this.lines.getChildren().clear();

    HBox line = this.createNewLine();
    List<List<byte[]>> words = this.splitWords();

    double usedWidth = 0;
    int elementsCounter = 0;
    for (List<byte[]> word : words)
    {
      double currentWordWidth = this.calculateWordWidth(word);

      line.autosize();
      usedWidth = line.getBoundsInParent().getWidth();
      if (!(currentWordWidth > contentWidth || (usedWidth + currentWordWidth) < contentWidth))
        line = this.createNewLine();

      boolean forceText = false;
      for (byte[] charByteArray : word)
      {
        line.autosize();
        usedWidth = line.getBoundsInParent().getWidth();
        InputStream is = this.getClass().getResourceAsStream(this.getSkinnable().getEmojisLocation() + charByteArrayToUnicode(charByteArray) + ".svg");
        if (is == null || forceText)
        {
          try
          {
            final Text text = new Text(new String(charByteArray, "UTF-8"));
            if (charByteArrayToUnicode(charByteArray).equals("FE0E") || charByteArrayToUnicode(charByteArray).equals("FE0F"))
              text.setFont(Font.font(0));
            else
              text.setFont(Font.font(this.getSkinnable().getFont().getFamily(), charByteArrayToUnicode(charByteArray).equals("0A") ? this.getSkinnable().getFont().getSize() / 2 : this.getSkinnable().getFont().getSize()));
            if (usedWidth + Math.ceil(text.getBoundsInParent().getWidth()) > contentWidth)
              line = this.createNewLine();
            text.setCursor(Cursor.TEXT);
            text.setFill(this.getSkinnable().getTextFill());
            for (final Range r : this.getSkinnable().getLinkRanges())
            {
              if (r.isInRange(elementsCounter))
              {
                text.setFill(this.getSkinnable().getLinkTextFill());
                text.setCursor(Cursor.HAND);
                text.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                  public void handle(MouseEvent event)
                  {
                    if (event.getButton().equals(MouseButton.PRIMARY))
                      r.getAssociatedAction().run();
                  }
                });
                break;
              }
            }
            if (this.selectedRange != null && this.selectedRange.isInRange(elementsCounter))
              text.setFill(this.getSkinnable().getSelectedTextFill());
            line.getChildren().add(text);
            forceText = false;
            if (charByteArrayToUnicode(charByteArray).equals("0A"))
              line = this.createNewLine();
            else if (charByteArrayToUnicode(charByteArray).equals("FE0E"))
              forceText = true;
          }
          catch (UnsupportedEncodingException e)
          {
            e.printStackTrace();
          }
        }
        else
        {
          final EmojiView emoji = new EmojiView(charByteArray);
          emoji.setPrefWidth(this.getSkinnable().getFont().getSize());
          this.getSkinnable().fontProperty().addListener(new ChangeListener<Font>()
          {
            public void changed(ObservableValue<? extends Font> observable, Font oldValue, Font newValue)
            {
              emoji.setPrefWidth(newValue.getSize());
            }
          });
          for (final Range r : this.getSkinnable().getLinkRanges())
          {
            if (r.isInRange(elementsCounter))
            {
              emoji.setOnMouseClicked(new EventHandler<MouseEvent>()
              {
                public void handle(MouseEvent event)
                {
                  r.getAssociatedAction().run();
                }
              });
              break;
            }
          }
          emoji.prefHeightProperty().bind(emoji.prefWidthProperty());
          if (usedWidth + emoji.getPrefWidth() > contentWidth)
            line = this.createNewLine();
          emoji.setCursor(Cursor.TEXT);
          line.getChildren().add(emoji);
        }
        elementsCounter++;
      }
    }
    this.lines.autosize();
  }

  private List<List<byte[]>> splitWords()
  {
    List<List<byte[]>> wordsList = new ArrayList<List<byte[]>>();

    List<byte[]> word = new ArrayList<byte[]>();
    for (int i = 0; i < this.charList.size(); i++)
    {
      word.add(this.charList.get(i));
      if (this.charList.get(i).length != 1 || this.charList.get(i)[0] == " ".getBytes()[0])
      {
        wordsList.add(word);
        word = new ArrayList<byte[]>();
      }
    }
    if (!word.isEmpty())
      wordsList.add(word);
    return wordsList;
  }

  private double calculateWordWidth(List<byte[]> word)
  {
    double totalWidth = 0;
    for (byte[] b : word)
    {
      try
      {
        Text text = new Text(new String(b, "UTF-8"));
        text.setFont(Font.font(this.getSkinnable().getFont().getFamily(), this.getSkinnable().getFont().getSize()));
        totalWidth += Math.ceil(text.getBoundsInParent().getWidth());
      }
      catch (UnsupportedEncodingException e)
      {
        e.printStackTrace();
      }
    }
    return totalWidth;
  }

  private void updateCharList(String text)
  {
    this.charList.clear();

    for (int i = 0; i < text.getBytes().length; i++)
    {
      String currentByteBinary = byteToBinary(text.getBytes()[i]);
      switch (currentByteBinary.indexOf('0'))
      {
        case 0:
          byte[] charByteArray = {text.getBytes()[i]};
          this.charList.add(charByteArray);
          break;
        case 2:
          byte[] charByteArray2 = {text.getBytes()[i], text.getBytes()[i + 1]};
          this.charList.add(charByteArray2);
          i++;
          break;
        case 3:
          byte[] charByteArray3 = {text.getBytes()[i], text.getBytes()[i + 1], text.getBytes()[i + 2]};
          this.charList.add(charByteArray3);
          i += 2;
          break;
        case 4:
          byte[] charByteArray4 = {text.getBytes()[i], text.getBytes()[i + 1], text.getBytes()[i + 2], text.getBytes()[i + 3]};
          this.charList.add(charByteArray4);
          i += 3;
          break;
      }
    }
  }

  private static String byteToBinary(byte byt)
  {
    return Integer.toBinaryString(byt & 255 | 256).substring(1);
  }

  private static String charByteArrayToUnicode(byte[] bytes)
  {
    switch (bytes.length)
    {
      case 1:
        return String.format("%02X", bytes[0]);
      case 2:
        return String.format("%02X", Integer.parseInt(byteToBinary(bytes[0]).substring(3) + byteToBinary(bytes[1]).substring(2), 2));
      case 3:
        return String.format("%02X", Integer.parseInt(byteToBinary(bytes[0]).substring(4) + byteToBinary(bytes[1]).substring(2) + byteToBinary(bytes[2]).substring(2), 2));
      case 4:
        return String.format("%02X", Integer.parseInt(byteToBinary(bytes[0]).substring(5) + byteToBinary(bytes[1]).substring(2) + byteToBinary(bytes[2]).substring(2) + byteToBinary(bytes[3]).substring(2), 2));
      default:
        return null;
    }
  }

  private class EmojiView extends VBox
  {
    private byte[] emojiBytes;
    private String emojiUnicode;
    private String emojiString;

    private ImageView imageView;

    public EmojiView(byte[] emojiBytes)
    {
      this.emojiBytes = emojiBytes;
      this.emojiUnicode = charByteArrayToUnicode(this.emojiBytes);
      try
      {
        this.emojiString = new String(emojiBytes, "UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
        this.emojiString = " ";
        e.printStackTrace();
      }

      this.setAlignment(Pos.CENTER);
      this.imageView = new ImageView();
      this.imageView.fitWidthProperty().bind(this.prefWidthProperty());
      this.imageView.fitHeightProperty().bind(this.prefHeightProperty());
      this.imageView.setDisable(true);
      this.getChildren().add(this.imageView);

      try
      {
        SVGDiagram diagram = SVGCache.getSVGUniverse().getDiagram(SVGCache.getSVGUniverse().loadSVG(this.getClass().getResourceAsStream(EmojisLabelSkin.this.getSkinnable().getEmojisLocation() + this.emojiUnicode + ".svg"), this.emojiUnicode));
        diagram.setIgnoringClipHeuristic(true);
        ChangeListener<Number> changeListener = new ChangeListener<Number>()
        {
          @Override
          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
          {
            if (EmojiView.this.getPrefWidth() > 0 && EmojiView.this.getPrefHeight() > 0)
            {
              try
              {
                WritableImage img = new WritableImage((int) EmojiView.this.getPrefWidth(), (int) EmojiView.this.getPrefHeight());
                BufferedImage swingImg = new BufferedImage((int) EmojiView.this.getPrefWidth(), (int) EmojiView.this.getPrefHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = (Graphics2D) swingImg.getGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.translate((int) (EmojiView.this.getPrefWidth() - EmojiView.this.getBoundsInParent().getWidth())/2, (int) (EmojiView.this.getPrefHeight() - EmojiView.this.getBoundsInParent().getHeight())/2);
                g2d.scale(EmojiView.this.getPrefWidth()/diagram.getWidth(), EmojiView.this.getPrefHeight()/diagram.getHeight());
                diagram.render(g2d);
                SwingFXUtils.toFXImage(swingImg, img);
                EmojiView.this.imageView.setImage(img);
              }
              catch (SVGException e)
              {
                e.printStackTrace();
              }
            }
          }
        };
        this.prefWidthProperty().addListener(changeListener);
        this.prefHeightProperty().addListener(changeListener);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    @Override
    public String toString()
    {
      return this.emojiString;
    }
  }
}
