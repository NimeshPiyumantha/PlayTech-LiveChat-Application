package com.PlayTechPvtLtd.LiveChatApplication.main.java.com.madeorsk.emojisfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.EnumConverter;
import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.css.converters.SizeConverter;
import com.sun.javafx.css.converters.URLConverter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class EmojisLabel extends Control
{
  protected static final String DEFAULT_EMOJIS_LOCATION = "/openmoji/";
  private List<Range> linkRanges = new ArrayList<Range>();

  public EmojisLabel()
  {
    this.getStyleClass().add("emojis-label");
    this.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
      EmojisLabel.this.requestLayout();
    });
  }

  public EmojisLabel(String text)
  {
    this();
    this.setText(text);
  }

  @Override
  protected Skin<?> createDefaultSkin()
  {
    return new EmojisLabelSkin(this);
  }

  public String getSelectedString()
  {
    if (this.getSkin() != null && this.getSkin() instanceof EmojisLabelSkin)
      return ((EmojisLabelSkin) this.getSkin()).getSelectedString();
    else
      return null;
  }

  public void addLinkRange(Range linkRange)
  {
    this.linkRanges.add(linkRange);
  }

  public boolean removeLinkRange(Range linkRange)
  {
    return this.linkRanges.remove(linkRange);
  }

  public List<Range> getLinkRanges()
  {
    return this.linkRanges;
  }

  /***************************************************************************
   *                                                                         *
   * Properties                                                              *
   *                                                                         *
   **************************************************************************/
  /**
   * The emojis internal directory location.
   */
  public final StringProperty emojisLocationProperty()
  {
    if (this.emojisLocation == null)
      this.emojisLocation = new SimpleStringProperty(this, "emojisLocation", DEFAULT_EMOJIS_LOCATION);
    return this.emojisLocation;
  }

  private StringProperty emojisLocation;

  public final void setEmojisLocation(String value)
  {
    this.emojisLocationProperty().setValue(value);
  }

  public final String getEmojisLocation()
  {
    return this.emojisLocation == null ? DEFAULT_EMOJIS_LOCATION : this.emojisLocation.getValue();
  }

  /**
   * The text to display in the label. The text may be null.
   */
  public final StringProperty textProperty()
  {
    if (text == null)
      text = new SimpleStringProperty(this, "text", "");
    return text;
  }

  private StringProperty text;

  public final void setText(String value)
  {
    textProperty().setValue(value);
  }

  public final String getText()
  {
    return text == null ? "" : text.getValue();
  }

  /**
   * Specifies how the text and graphic within the Labeled should be
   * aligned when there is empty space within the Labeled.
   */
  public final ObjectProperty<Pos> alignmentProperty()
  {
    if (alignment == null)
    {
      alignment = new StyleableObjectProperty<Pos>(Pos.CENTER_LEFT)
      {
        @Override
        public CssMetaData<EmojisLabel, Pos> getCssMetaData()
        {
          return StyleableProperties.ALIGNMENT;
        }

        @Override
        public Object getBean()
        {
          return EmojisLabel.this;
        }

        @Override
        public String getName()
        {
          return "alignment";
        }
      };
    }
    return alignment;
  }

  private ObjectProperty<Pos> alignment;

  public final void setAlignment(Pos value)
  {
    alignmentProperty().set(value);
  }

  public final Pos getAlignment()
  {
    return alignment == null ? Pos.CENTER_LEFT : alignment.get();
  }

  /**
   * Specifies the behavior for lines of text <em>when text is multiline</em>
   * Unlike  which affects the graphic and text, this setting
   * only affects multiple lines of text relative to the text bounds.
   */
  public final ObjectProperty<TextAlignment> textAlignmentProperty()
  {
    if (textAlignment == null)
    {
      textAlignment = new StyleableObjectProperty<TextAlignment>(TextAlignment.LEFT)
      {
        @Override
        public CssMetaData<EmojisLabel, TextAlignment> getCssMetaData()
        {
          return StyleableProperties.TEXT_ALIGNMENT;
        }

        @Override
        public Object getBean()
        {
          return EmojisLabel.this;
        }

        @Override
        public String getName()
        {
          return "textAlignment";
        }
      };
    }
    return textAlignment;
  }

  private ObjectProperty<TextAlignment> textAlignment;

  public final void setTextAlignment(TextAlignment value)
  {
    textAlignmentProperty().setValue(value);
  }

  public final TextAlignment getTextAlignment()
  {
    return textAlignment == null ? TextAlignment.LEFT : textAlignment.getValue();
  }

  /**
   * The default font to use for text in the Labeled. If the Label's text is
   * rich text then this font may or may not be used depending on the font
   * information embedded in the rich text, but in any case where a default
   * font is required, this font will be used.
   */
  public final ObjectProperty<Font> fontProperty()
  {
    if (font == null)
    {
      font = new StyleableObjectProperty<Font>(Font.getDefault())
      {
        private boolean fontSetByCss = false;

        @Override
        public void applyStyle(StyleOrigin newOrigin, Font value)
        {
          //
          // RT-20727 - if CSS is setting the font, then make sure invalidate doesn't call impl_reapplyCSS
          //
          try
          {
            // super.applyStyle calls set which might throw if value is bound.
            // Have to make sure fontSetByCss is reset.
            fontSetByCss = true;
            super.applyStyle(newOrigin, value);
          }
          catch (Exception e)
          {
            throw e;
          }
          finally
          {
            fontSetByCss = false;
          }
        }

        @Override
        public void set(Font value)
        {
          final Font oldValue = get();
          if (value != null ? !value.equals(oldValue) : oldValue != null)
          {
            super.set(value);
          }
        }

        @Override
        protected void invalidated()
        {
          // RT-20727 - if font is changed by calling setFont, then
          // css might need to be reapplied since font size affects
          // calculated values for styles with relative values
          if (fontSetByCss == false)
          {
            EmojisLabel.this.impl_reapplyCSS();
          }
        }

        @Override
        public CssMetaData<EmojisLabel, Font> getCssMetaData()
        {
          return StyleableProperties.FONT;
        }

        @Override
        public Object getBean()
        {
          return EmojisLabel.this;
        }

        @Override
        public String getName()
        {
          return "font";
        }
      };
    }
    return font;
  }

  private ObjectProperty<Font> font;

  public final void setFont(Font value)
  {
    fontProperty().setValue(value);
  }

  public final Font getFont()
  {
    return font == null ? Font.getDefault() : font.getValue();
  }

  /**
   * Whether all text should be underlined.
   */
  public final BooleanProperty underlineProperty()
  {
    if (underline == null)
    {
      underline = new StyleableBooleanProperty(false)
      {
        @Override
        public CssMetaData<EmojisLabel, Boolean> getCssMetaData()
        {
          return StyleableProperties.UNDERLINE;
        }

        @Override
        public Object getBean()
        {
          return EmojisLabel.this;
        }

        @Override
        public String getName()
        {
          return "underline";
        }
      };
    }
    return underline;
  }

  private BooleanProperty underline;

  public final void setUnderline(boolean value)
  {
    underlineProperty().setValue(value);
  }

  public final boolean isUnderline()
  {
    return underline == null ? false : underline.getValue();
  }

  /**
   * Specifies the space in pixel between lines.
   *
   * @since JavaFX 8.0
   */
  public final DoubleProperty lineSpacingProperty()
  {
    if (lineSpacing == null)
    {
      lineSpacing = new StyleableDoubleProperty(0)
      {
        @Override
        public CssMetaData<EmojisLabel, Number> getCssMetaData()
        {
          return StyleableProperties.LINE_SPACING;
        }

        @Override
        public Object getBean()
        {
          return EmojisLabel.this;
        }

        @Override
        public String getName()
        {
          return "lineSpacing";
        }
      };
    }
    return lineSpacing;
  }

  private DoubleProperty lineSpacing;

  public final void setLineSpacing(double value)
  {
    lineSpacingProperty().setValue(value);
  }

  public final double getLineSpacing()
  {
    return lineSpacing == null ? 0 : lineSpacing.getValue();
  }

  /**
   * The {@link Paint} used to fill the text.
   */
  private ObjectProperty<Paint> textFill;

  public final void setTextFill(Paint value)
  {
    textFillProperty().set(value);
  }

  public final Paint getTextFill()
  {
    return textFill == null ? Color.BLACK : textFill.get();
  }

  public final ObjectProperty<Paint> textFillProperty()
  {
    if (textFill == null)
    {
      textFill = new StyleableObjectProperty<Paint>(Color.BLACK)
      {
        @Override
        public CssMetaData<EmojisLabel, Paint> getCssMetaData()
        {
          return StyleableProperties.TEXT_FILL;
        }

        @Override
        public Object getBean()
        {
          return EmojisLabel.this;
        }

        @Override
        public String getName()
        {
          return "textFill";
        }
      };
    }
    return textFill;
  }

  /**
   * The {@link Paint} used to fill the selection background.
   */
  private ObjectProperty<Paint> selectionFill;

  public final void setSelectionFill(Paint value)
  {
    this.selectionFillProperty().set(value);
  }

  public final Paint getSelectionFill()
  {
    return this.selectionFill == null ? Color.CORNFLOWERBLUE : this.selectionFill.get();
  }

  public final ObjectProperty<Paint> selectionFillProperty()
  {
    if (this.selectionFill == null)
    {
      this.selectionFill = new StyleableObjectProperty<Paint>(Color.CORNFLOWERBLUE)
      {
        @Override
        public CssMetaData<EmojisLabel, Paint> getCssMetaData()
        {
          return StyleableProperties.SELECTION_FILL;
        }

        @Override
        public Object getBean()
        {
          return EmojisLabel.this;
        }

        @Override
        public String getName()
        {
          return "selectionFill";
        }
      };
    }
    return this.selectionFill;
  }

  /**
   * The {@link Paint} used to fill the selected text.
   */
  private ObjectProperty<Paint> selectedTextFill;

  public final void setSelectedTextFill(Paint value)
  {
    this.selectedTextFillProperty().set(value);
  }

  public final Paint getSelectedTextFill()
  {
    return this.selectedTextFill == null ? Color.WHITE : this.selectedTextFill.get();
  }

  public final ObjectProperty<Paint> selectedTextFillProperty()
  {
    if (this.selectedTextFill == null)
    {
      this.selectedTextFill = new StyleableObjectProperty<Paint>(Color.WHITE)
      {
        @Override
        public CssMetaData<EmojisLabel, Paint> getCssMetaData()
        {
          return StyleableProperties.SELECTED_TEXT_FILL;
        }

        @Override
        public Object getBean()
        {
          return EmojisLabel.this;
        }

        @Override
        public String getName()
        {
          return "selectedTextFill";
        }
      };
    }
    return this.selectedTextFill;
  }

  /**
   * The {@link Paint} used to fill a link into text.
   */
  private ObjectProperty<Paint> linkTextFill;

  public final void setLinkTextFill(Paint value)
  {
    this.linkTextFillProperty().set(value);
  }

  public final Paint getLinkTextFill()
  {
    return this.linkTextFill == null ? Color.CORNFLOWERBLUE : this.linkTextFill.get();
  }

  public final ObjectProperty<Paint> linkTextFillProperty()
  {
    if (this.linkTextFill == null)
    {
      this.linkTextFill = new StyleableObjectProperty<Paint>(Color.CORNFLOWERBLUE)
      {
        @Override
        public CssMetaData<EmojisLabel, Paint> getCssMetaData()
        {
          return StyleableProperties.LINK_TEXT_FILL;
        }

        @Override
        public Object getBean()
        {
          return EmojisLabel.this;
        }

        @Override
        public String getName()
        {
          return "linkTextFill";
        }
      };
    }
    return this.linkTextFill;
  }

  @Override
  public String toString()
  {
    StringBuilder builder =
        new StringBuilder(super.toString())
            .append("'").append(getText()).append("'");
    return builder.toString();
  }

  /***************************************************************************
   *                                                                         *
   * Stylesheet Handling                                                     *
   *                                                                         *
   **************************************************************************/

  /**
   * Not everything uses the default value of false for alignment.
   * This method provides a way to have them return the correct initial value.
   *
   * @treatAsPrivate implementation detail
   */
  @Deprecated
  protected Pos impl_cssGetAlignmentInitialValue()
  {
    return Pos.CENTER_LEFT;
  }

  /**
   * @treatAsPrivate implementation detail
   */
  private static class StyleableProperties
  {
    private static final FontCssMetaData<EmojisLabel> FONT =
        new FontCssMetaData<EmojisLabel>("-fx-font", Font.getDefault())
        {
          @Override
          public boolean isSettable(EmojisLabel n)
          {
            return n.font == null || !n.font.isBound();
          }

          @Override
          public StyleableProperty<Font> getStyleableProperty(EmojisLabel n)
          {
            return (StyleableProperty<Font>) (WritableValue<Font>) n.fontProperty();
          }
        };

    private static final CssMetaData<EmojisLabel, Pos> ALIGNMENT =
        new CssMetaData<EmojisLabel, Pos>("-fx-alignment",
            new EnumConverter<Pos>(Pos.class), Pos.CENTER_LEFT)
        {
          @Override
          public boolean isSettable(EmojisLabel n)
          {
            return n.alignment == null || !n.alignment.isBound();
          }

          @Override
          public StyleableProperty<Pos> getStyleableProperty(EmojisLabel n)
          {
            return (StyleableProperty<Pos>) (WritableValue<Pos>) n.alignmentProperty();
          }

          @Override
          public Pos getInitialValue(EmojisLabel n)
          {
            return n.impl_cssGetAlignmentInitialValue();
          }
        };

    private static final CssMetaData<EmojisLabel, TextAlignment> TEXT_ALIGNMENT =
        new CssMetaData<EmojisLabel, TextAlignment>("-fx-text-alignment",
            new EnumConverter<TextAlignment>(TextAlignment.class),
            TextAlignment.LEFT)
        {
          @Override
          public boolean isSettable(EmojisLabel n)
          {
            return n.textAlignment == null || !n.textAlignment.isBound();
          }

          @Override
          public StyleableProperty<TextAlignment> getStyleableProperty(EmojisLabel n)
          {
            return (StyleableProperty<TextAlignment>) (WritableValue<TextAlignment>) n.textAlignmentProperty();
          }
        };

    private static final CssMetaData<EmojisLabel, Paint> TEXT_FILL =
        new CssMetaData<EmojisLabel, Paint>("-fx-text-fill",
            PaintConverter.getInstance(), Color.BLACK)
        {
          @Override
          public boolean isSettable(EmojisLabel n)
          {
            return n.textFill == null || !n.textFill.isBound();
          }

          @Override
          public StyleableProperty<Paint> getStyleableProperty(EmojisLabel n)
          {
            return (StyleableProperty<Paint>) (WritableValue<Paint>) n.textFillProperty();
          }
        };

    private static final CssMetaData<EmojisLabel, Boolean> UNDERLINE =
        new CssMetaData<EmojisLabel, Boolean>("-fx-underline",
            BooleanConverter.getInstance(), Boolean.FALSE)
        {
          @Override
          public boolean isSettable(EmojisLabel n)
          {
            return n.underline == null || !n.underline.isBound();
          }

          @Override
          public StyleableProperty<Boolean> getStyleableProperty(EmojisLabel n)
          {
            return (StyleableProperty<Boolean>) (WritableValue<Boolean>) n.underlineProperty();
          }
        };

    private static final CssMetaData<EmojisLabel, Number> LINE_SPACING =
        new CssMetaData<EmojisLabel, Number>("-fx-line-spacing",
            SizeConverter.getInstance(), 0)
        {
          @Override
          public boolean isSettable(EmojisLabel n)
          {
            return n.lineSpacing == null || !n.lineSpacing.isBound();
          }

          @Override
          public StyleableProperty<Number> getStyleableProperty(EmojisLabel n)
          {
            return (StyleableProperty<Number>) (WritableValue<Number>) n.lineSpacingProperty();
          }
        };

    private static final CssMetaData<EmojisLabel, Paint> SELECTION_FILL =
        new CssMetaData<EmojisLabel, Paint>("-efx-selection-fill",
            PaintConverter.getInstance(), Color.CORNFLOWERBLUE)
        {
          @Override
          public boolean isSettable(EmojisLabel n)
          {
            return n.selectionFill == null || !n.selectionFill.isBound();
          }

          @Override
          public StyleableProperty<Paint> getStyleableProperty(EmojisLabel n)
          {
            return (StyleableProperty<Paint>) (WritableValue<Paint>) n.selectionFillProperty();
          }
        };

    private static final CssMetaData<EmojisLabel, Paint> SELECTED_TEXT_FILL =
        new CssMetaData<EmojisLabel, Paint>("-efx-selected-text-fill",
            PaintConverter.getInstance(), Color.WHITE)
        {
          @Override
          public boolean isSettable(EmojisLabel n)
          {
            return n.selectedTextFill == null || !n.selectedTextFill.isBound();
          }

          @Override
          public StyleableProperty<Paint> getStyleableProperty(EmojisLabel n)
          {
            return (StyleableProperty<Paint>) (WritableValue<Paint>) n.selectedTextFillProperty();
          }
        };

    private static final CssMetaData<EmojisLabel, Paint> LINK_TEXT_FILL =
        new CssMetaData<EmojisLabel, Paint>("-efx-link-text-fill",
            PaintConverter.getInstance(), Color.CORNFLOWERBLUE)
        {
          @Override
          public boolean isSettable(EmojisLabel n)
          {
            return n.linkTextFill == null || !n.linkTextFill.isBound();
          }

          @Override
          public StyleableProperty<Paint> getStyleableProperty(EmojisLabel n)
          {
            return (StyleableProperty<Paint>) (WritableValue<Paint>) n.linkTextFillProperty();
          }
        };

    private static final CssMetaData<EmojisLabel, String> EMOJIS_LOCATION =
        new CssMetaData<EmojisLabel, String>("-efx-emojis-location", URLConverter.getInstance(), DEFAULT_EMOJIS_LOCATION)
        {
          @Override
          public boolean isSettable(EmojisLabel styleable)
          {
            return styleable.emojisLocation == null || !styleable.emojisLocation.isBound();
          }

          @Override
          public StyleableProperty<String> getStyleableProperty(EmojisLabel styleable)
          {
            return (StyleableProperty<String>) (WritableValue<String>) styleable.emojisLocation;
          }
        };

    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    static
    {
      final List<CssMetaData<? extends Styleable, ?>> styleables =
          new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
      Collections.addAll(styleables,
          FONT,
          ALIGNMENT,
          TEXT_ALIGNMENT,
          TEXT_FILL,
          UNDERLINE,
          LINE_SPACING,
          SELECTION_FILL,
          SELECTED_TEXT_FILL,
          LINK_TEXT_FILL,
          EMOJIS_LOCATION
      );
      STYLEABLES = Collections.unmodifiableList(styleables);
    }
  }

  /**
   * @return The CssMetaData associated with this class, which may include the
   * CssMetaData of its super classes.
   * @since JavaFX 8.0
   */
  public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData()
  {
    return StyleableProperties.STYLEABLES;
  }

  /**
   * {@inheritDoc}
   *
   * @since JavaFX 8.0
   */
  @Override
  public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData()
  {
    return getClassCssMetaData();
  }
}
