package com.PlayTechPvtLtd.LiveChatApplication.main.java.com.madeorsk.emojisfx;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class EmojisLabelBehavior extends BehaviorBase<EmojisLabel>
{
  protected static List<KeyBinding> EMOJISLABEL_BINDINGS = new ArrayList<KeyBinding>();

  static
  {
    EMOJISLABEL_BINDINGS.add(new KeyBinding(KeyCode.A, "SelectAll").ctrl());
    EMOJISLABEL_BINDINGS.add(new KeyBinding(KeyCode.C, "Copy").ctrl());
    EMOJISLABEL_BINDINGS.addAll(BehaviorBase.TRAVERSAL_BINDINGS);
  }

  protected static ContextMenu CONTEXT_MENU = new ContextMenu();

  private EmojisLabelSkin skin;

  private StringProperty selectedString = new SimpleStringProperty(this, "selectedString", "");

  private double originX;
  private double originY;
  private int[] originNodeCoords = {-1, -1};
  public int lineNumberOnLastUpdate = -1;
  public int nodeNumberOnLastUpdate = -1;

  public EmojisLabelBehavior(EmojisLabel control)
  {
    super(control, EMOJISLABEL_BINDINGS);
  }

  public void setSkin(EmojisLabelSkin skin)
  {
    this.skin = skin;
  }

  public void setSelectedString(String str)
  {
    this.selectedString.set(str);
  }

  public String getSelectedString()
  {
    return this.selectedString.getValue();
  }

  @Override
  protected void callAction(String name)
  {
    super.callAction(name);
    if (name.equals("SelectAll"))
      this.selectAll();
    else if (name.equals("Copy"))
      this.copy();
  }

  @Override
  public void mousePressed(MouseEvent e)
  {
    super.mousePressed(e);
    this.getControl().requestFocus();
    if (e.getButton().equals(MouseButton.PRIMARY))
    {
      this.clearSelection();

      if (!(e.getTarget() instanceof HBox) && e.getTarget() instanceof Node)
      {
        this.originX = e.getX();
        this.originY = e.getY();
      }

      int lineNumber = this.skin.findLineNumberFromY(this.originY);
      if (lineNumber >= 0)
      {
        HBox line = this.skin.getLine(lineNumber);
        int nodeNumber = this.skin.findNodeNumberFromX(line, this.originX);
        if (nodeNumber >= 0)
        {
          Node node = line.getChildren().get(nodeNumber);

          this.originNodeCoords[0] = lineNumber;
          this.originNodeCoords[1] = nodeNumber;

          if (e.getClickCount() == 2 || e.getClickCount() == 3)
            this.skin.evaluateSelection(node, nodeNumber, line, lineNumber, node, nodeNumber, line, lineNumber, e.getClickCount());
        }
      }
    }
  }

  @Override
  public void contextMenuRequested(ContextMenuEvent e)
  {
    super.contextMenuRequested(e);
    if (CONTEXT_MENU.isShowing())
      CONTEXT_MENU.hide();
    CONTEXT_MENU.getItems().clear();
    MenuItem selectAllItem = new MenuItem("Select all");
    selectAllItem.setOnAction(new EventHandler<ActionEvent>()
    {
      public void handle(ActionEvent event)
      {
        EmojisLabelBehavior.this.callAction("SelectAll");
      }
    });
    MenuItem copyItem = new MenuItem("Copy");
    copyItem.setOnAction(new EventHandler<ActionEvent>()
    {
      public void handle(ActionEvent event)
      {
        EmojisLabelBehavior.this.callAction("Copy");
      }
    });
    CONTEXT_MENU.getItems().addAll(selectAllItem, copyItem);
    CONTEXT_MENU.show(((Node) e.getTarget()).getParent(), e.getScreenX(), e.getScreenY());
  }

  @Override
  public void mouseDragged(MouseEvent e)
  {
    super.mouseDragged(e);
    int originLineNumber = this.skin.findLineNumberFromY(this.originY);
    if (originLineNumber < 0)
      return;
    HBox originLine = this.skin.getLine(originLineNumber);
    int originNodeNumber = this.skin.findNodeNumberFromX(originLine, this.originX);
    if (originNodeNumber < 0)
      return;
    Node originNode = originLine.getChildren().get(originNodeNumber);

    int currentLineNumber = this.skin.findLineNumberFromY(e.getY());
    if (currentLineNumber >= 0)
    {
      HBox currentLine = this.skin.getLine(currentLineNumber);
      int currentNodeNumber = this.skin.findNodeNumberFromX(currentLine, e.getX());

      if (currentLineNumber == this.lineNumberOnLastUpdate && currentNodeNumber == this.nodeNumberOnLastUpdate)
        return;

      if (currentNodeNumber >= 0)
        this.skin.evaluateSelection(originNode, originNodeNumber, originLine, originLineNumber, currentLine.getChildren().get(currentNodeNumber), currentNodeNumber, currentLine, currentLineNumber, e.getClickCount());
    }
  }

  @Override
  public void mouseReleased(MouseEvent e)
  {
    super.mouseReleased(e);
    if (e.getButton().equals(MouseButton.PRIMARY))
    {
      if (this.originNodeCoords[0] >= 0 && this.originNodeCoords[1] >= 0)
      {
        int lineNumber = this.skin.findLineNumberFromY(this.originY);
        int nodeNumber = this.skin.findNodeNumberFromX(this.skin.getLine(lineNumber), this.originX);
        if (lineNumber == this.originNodeCoords[0] && nodeNumber == this.originNodeCoords[1])
        {
          Node node = this.skin.getNode(lineNumber, nodeNumber);
          if (node != null)
            if (node.getOnMouseClicked() != null)
              node.getOnMouseClicked().handle(e);
        }
      }
      this.originNodeCoords[0] = -1;
      this.originNodeCoords[1] = -1;
      this.lineNumberOnLastUpdate = -1;
      this.nodeNumberOnLastUpdate = -1;
    }
  }

  @Override
  protected void focusChanged()
  {
    super.focusChanged();
    this.clearSelection();
  }

  private void selectAll()
  {
    this.skin.selectAll();
  }

  private void copy()
  {
    if (this.selectedString.getValue() != null && !this.selectedString.getValue().isEmpty())
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.selectedString.getValue()), null);
  }

  private void clearSelection()
  {
    this.skin.clearSelectionGraphics();
    this.selectedString.set("");
  }
}
