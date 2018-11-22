package controls;

import com.sun.java.accessibility.util.EventID;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

/**
 *
 * @author
 */
public class DateTimeFieldSkin extends BehaviorSkinBase<DateTimeField, BehaviorBase<DateTimeField>> {
    
    protected DateTimeField.ValueKind determineType() {
        return getSkinnable().getKind();
    }
        
    final private HBox textGroup = new HBox();
    final private NumericTextField dayField;
    final private NumericTextField monthField;
    final private NumericTextField yearField;
    final private NumericTextField hoursField;
    final private NumericTextField minutesField;
    final private NumericTextField secondsField;
    final private Label dot1 = new Label( "." );
    final private Label dot2 = new Label( "." );
    final private Label colon1 = new Label( ":" );
    final private Label colon2 = new Label( ":" );
    final private Label timeSign = new Label( " T " );
    protected final ObservableObjectValue<FontMetrics> fontMetrics;
    private ObservableList<Control> dateFields;
    private ObservableList<Control> timeFields;
    private List<NumericTextField> valueFields = new ArrayList<>();
    private boolean hasFocus = false;
    /**
     * Set fake focus on DateTimeField.
     */
    private InvalidationListener focusInvalidator = new InvalidationListener() {
        @Override
        public void invalidated( Observable observable ) {            
            Node focusNode = textGroup.getChildren().stream()
                    .filter( Node::isFocused ).findFirst().orElse( null );
            hasFocus = focusNode != null;
            getSkinnable().setFakeFocus( hasFocus );
        }
    };
    private boolean inUpdate = false;
    //for tests
    private Border baseBorder = new Border( new BorderStroke( 
            Color.GRAY,
            BorderStrokeStyle.SOLID, 
            CornerRadii.EMPTY, 
            BorderWidths.DEFAULT
    ) );
    
    /**
     * For month styling.
     */
    private InvalidationListener monthStyleInvalidator = new InvalidationListener() {
        @Override
        public void invalidated( Observable observable ) {
            TextStyle newStyle = getSkinnable().getMonthStyle();
            boolean showDots = dot1.isVisible() && dot2.isVisible() && newStyle == null;
            ( (MonthField) monthField ).setTextStyle( newStyle );
            if( showDots ) { dot1.setText( "." ); dot2.setText( "." ); }
            else { dot1.setText( " " ); dot2.setText( " " ); }
        }
    };
    
    /**
     * Whenever value of DateTimeField.dateTimeValue changes update text 
     * of underlying controls(TextFields)
     */
    final protected void updateTextFields() {
        LocalDateTime dateValue = getSkinnable().getDateTimeValue();
        if( dateValue == null ) {
            valueFields.forEach( c -> {c.setValue( null );} );
            return;
        }
        dayField.setValue( dateValue.getDayOfMonth() );
        monthField.setValue( dateValue.getMonthValue() );
        yearField.setValue( dateValue.getYear() );
        hoursField.setValue( dateValue.getHour() );
        minutesField.setValue( dateValue.getMinute() );
        secondsField.setValue( dateValue.getSecond() );
    }
    
    /**
     * Convinient method for creating NumericTextFields.
     * 
     * @param highLimit
     * @return NumericTextField
     */
    private NumericTextField createTextField( Integer highLimit ) {
        NumericTextField result = new NumericTextField(highLimit);
        customizeField( result );
        return result;
    }
    protected void customizeField( NumericTextField result  ) {        
        result.focusedProperty().addListener( focusInvalidator );
        result.setMouseTransparent( true );
        //this for navigation buttons
        result.addEventFilter( KeyEvent.KEY_PRESSED, ( event ) -> {
            switch( event.getCode() ) {
                case LEFT:
                    handleNavigation(false);
                    break;
                case RIGHT:
                    handleNavigation(true);
                    break;
            }
        });        
    }
    
    public DateTimeFieldSkin( final DateTimeField control ) {
        this( control, new DateTimeFieldBehavior( control ) );
    }
    
    public DateTimeFieldSkin( final DateTimeField control, DateTimeFieldBehavior behavior ) {        
        super( control, behavior );
        behavior.setSkin( this );
        
        //Create TextFields associated with DateTimeField.dateValue
        dayField = createTextField( 31 );
        monthField = new MonthField();
        customizeField( monthField );
        yearField = createTextField( 2100 );//for new "millenium issue", now it will be "century"
        //Add them to separate filtered collection
        //Bind visible property with DateTimeField.dateKindProperty
        ObservableList<Control> dateCollection = FXCollections.observableArrayList( 
                dayField, dot1, monthField, dot2, yearField 
        );
        dateCollection.forEach( ( c ) -> {
            c.visibleProperty().bind( getSkinnable().dateKindProperty() );
        } );
        dateFields = new FilteredList<>( dateCollection,                
                ( t ) -> { return t.isVisible(); }
        );        
        //Create TextFields associated with DateTimeField.timeValue
        hoursField = createTextField( 23 );
        minutesField = createTextField( 59 );
        secondsField = createTextField( 59 );
        
        ObservableList<Control> timeCollection = FXCollections.observableArrayList( hoursField, colon1, minutesField, colon2, secondsField );
        timeCollection.forEach( ( c ) -> {
            c.visibleProperty().bind( getSkinnable().timeKindProperty() );
        } );
        timeFields = new FilteredList<>( timeCollection,
                (t ) -> { return t.isVisible(); }
        );
        
        timeSign.visibleProperty().bind( getSkinnable().timeKindProperty() );
        Font oldFont = timeSign.getFont();
        if( oldFont == null ) oldFont = Font.getDefault();
        timeSign.setFont( Font.font( oldFont.getFamily(), FontWeight.BOLD, oldFont.getSize() ) );
               
        textGroup.setAlignment( Pos.CENTER_RIGHT );        
        textGroup.setFocusTraversable( false );
        textGroup.setManaged( false );
//        textGroup.setBorder( baseBorder );
        
        getChildren().add( textGroup );
        
        //for width calculattion
        fontMetrics = new ObjectBinding<FontMetrics>() {
            { bind( getSkinnable().getFontProperty() ); }
            @Override
            protected FontMetrics computeValue() {
                return Toolkit.getToolkit().getFontLoader().getFontMetrics( getSkinnable().getFont() );
            }
        };
        
        getSkinnable().dateTimeValueProperty().addListener( 
                (observable ) -> {                    
                    inUpdate = true;
                    try {
                        updateTextFields();
                    } finally {
                        inUpdate = false;
                    }
                }
        );
        
        getSkinnable().monthStyleProperty().addListener( monthStyleInvalidator );
        //for the convenience       
        valueFields.addAll( 
                Arrays.asList( dayField, monthField, yearField, hoursField, minutesField, secondsField )
        );
        //finally update text to reflect value that has been passed through constructor
        updateTextFields();        
    }

    @Override
    protected void layoutChildren( double contentX, double contentY, 
            double contentWidth, double contentHeight ) {
        
        textGroup.resizeRelocate( contentX, contentY, contentWidth, contentHeight);     

        boolean showDateFields = getSkinnable().isDate();
        boolean showTimeFields = getSkinnable().isTime();
        ObservableList<Node> children = textGroup.getChildren();
        children.clear();
        if( showDateFields ) children.addAll( dateFields );
        if( showDateFields && showTimeFields ) children.add( timeSign );
        if( showTimeFields ) children.addAll( timeFields );
    }
    
    @Override
    protected double computeMaxHeight( double width, double topInset, 
            double rightInset, double bottomInset, double leftInset ) {
        return computePrefHeight( width, topInset, rightInset, bottomInset, leftInset );
    }

    @Override
    protected double computeMaxWidth( double height, double topInset, 
            double rightInset, double bottomInset, double leftInset ) {        
        return super.computeMaxWidth( height, topInset, rightInset, bottomInset, leftInset );
    }

    @Override
    protected double computeMinHeight( double width, double topInset, 
            double rightInset, double bottomInset, double leftInset ) {
        return computePrefHeight( width, topInset, rightInset, bottomInset, leftInset );
    }

    @Override
    protected double computeMinWidth( double height, double topInset, 
            double rightInset, double bottomInset, double leftInset ) {
        return computePrefWidth( height, topInset, rightInset, bottomInset, leftInset );
    }

    @Override
    protected double computePrefHeight( double width, double topInset, 
            double rightInset, double bottomInset, double leftInset ) {        
        return topInset + bottomInset + fontMetrics.get().getLineHeight();
    }

    @Override
    protected double computePrefWidth( double height, double topInset, 
            double rightInset, double bottomInset, double leftInset ) {        
        super.computePrefWidth( height, topInset, rightInset, bottomInset, leftInset );
        double w = fontMetrics.get().computeStringWidth( "0" );        
        double dateW = dayField.getColumnCount() * w 
                + monthField.getColumnCount() * w
                + yearField.getColumnCount() * w
//                + dot1.getWidth() + dot2.getWidth(); //for first time dot1.getWidth() = 0
                + 2 * w;
        double timeW = hoursField.getColumnCount() * w
                + minutesField.getColumnCount() * w
                + secondsField.getColumnCount() * w
                + colon1.getWidth() + colon2.getWidth();
        return leftInset + rightInset 
                + ( getSkinnable().isDate() ? dateW : 0 )
                + ( getSkinnable().isTime() ? timeW : 0 ) 
                + ( getSkinnable().isDate() && getSkinnable().isTime() ? timeSign.getWidth() : 0 );
    }
    
    /**
     * Passes focus to TextFields. As TextFields can occupy less area then whole DateTimeField
     * we calculate click before and after them and select first or last TextField 
     * accordingly.
     * @param e MouseEvent
     */
    protected void receiveMousePressed( MouseEvent e ) {
        if( !textGroup.getChildren().isEmpty() ) {           
            Node focusedNode = textGroup.getChildren().stream().filter( ( node ) -> {                
                Bounds nodeBounds = node.localToScreen( node.getBoundsInLocal() );                
                return ( e.getScreenX() <= nodeBounds.getMinX() 
                        || e.getScreenX() <= nodeBounds.getMaxX() 
                        )
                        && node.isVisible()
                        && ( node instanceof NumericTextField );
            } ).findFirst().orElseGet( () -> {
                Node result;
                for( int i = textGroup.getChildren().size()-1; 0 <= i; i--  ) {
                    result = textGroup.getChildren().get( i );
                    if( result.isVisible() 
                            && ( result instanceof NumericTextField ) )
                        return result;
                }
                return null;
            } );
            if( focusedNode != null ) focusedNode.requestFocus();
        }       
    }
    
    /**
     * Calls after loosing focus. Validates value that had been retreived from TextFields
     * and updates DateTimeField.dateTimeValue separately.
     */
    protected void updateControlValue() {
        
        //System.out.println( "updateControlValue" );
        
        Predicate<Control> hasNullValue = ( c ) -> {
            return c instanceof NumericTextField 
                    && ( ( NumericTextField )c ).getValue() == null;
        };
        
        try {
            if(dateFields.size() > 0) {
                if( dateFields.stream().filter( hasNullValue ).count() != 0 )
                    throw new DateTimeException( "One of the date fields has null value." ); 
                Integer yearValue = yearField.getValue();
                Integer dayValue = dayField.getValue();
                Month month = Month.of( monthField.getValue() );
                int maxMonthDay = month.minLength();;
                if( Year.of( yearValue ).isLeap() ) 
                    maxMonthDay = month.maxLength();
                if( maxMonthDay < dayValue )
                    dayValue = maxMonthDay;
                LocalDate resultDate = LocalDate.of( yearValue, 
                        month, dayValue);
                getSkinnable().setDateValue( resultDate );
            }
            if(timeFields.size() > 0) {
                if(timeFields.stream().filter( hasNullValue ).count() != 0)
                    throw new DateTimeException( "One of the time fields has null value." );                    
                LocalTime resTime = LocalTime.of( hoursField.getValue(), 
                        minutesField.getValue(), secondsField.getValue());
                getSkinnable().setTimeValue( resTime );
            }
                
        } catch( DateTimeException ex ) {
            //logging
            getSkinnable().setDateTimeValue( null );
        }        
    }

    private void handleNavigation( boolean forward ) {
        Consumer<? super Control> cons = new Consumer<Control>() {
            private Control prev;
            private boolean gotIt = false;
            @Override
            public void accept( Control c ) {
                if( gotIt ) {
                    c.requestFocus();
                    gotIt = false;
                } else {
                    gotIt = c.isFocused();
                    if( gotIt && !forward ) {
                        if(  prev != null  ) prev.requestFocus();
                        gotIt = false;
                    }
                }
                prev = c;
            }
        };
        valueFields.stream().forEachOrdered( cons );
    }
    
    /**
     * TODO: needs reimplementation (value property).
     */
    protected static class MonthField extends NumericTextField {

        private StringConverter<Integer> defConverter;
        private TextStyle oldTextStyle;
        
        private StringConverter<Integer> newConverter = new StringConverter<Integer>() {
            @Override
            public String toString( Integer object ) {
                if( getTextStyle() == null && defConverter != null )
                    return defConverter.toString( object );
                if( getTextStyle() != null && object != null && 0 < object && object < 12 ) 
                    return Month.of( object )
                            .getDisplayName( getTextStyle(), getTextLocale() );
                return "";
            }

            @Override
            public Integer fromString( String string ) {                
                if( string == null || "".equals( string.trim() ) ) return null;
                /**
                 * Workarround for TextFormatter.Change.apply and value property
                 */
                boolean isDigit = string.chars().allMatch( Character::isDigit );
                if( getTextStyle() == null && defConverter != null && isDigit)
                    return defConverter.fromString( string );
                //this is for numeric values, when we change converter it is needed 
                //to decode old value ( see TextFormatter.Change.apply )
                if( isDigit )
                    return Integer.valueOf( string );
                //all this stuff need to remove
                TextStyle style = getTextStyle();
                if( style == null || oldTextStyle != null ) style = oldTextStyle;
                if( style != null )
                    for( Month month : Month.values() )
                        if( month
                                .getDisplayName( style, getTextLocale() )
                                .equals( string ) ) {
                            return month.getValue();
                        }
                return null;
            }
        };
        
        public MonthField() {
            super(12);
            //save old default converter (some kind of a cheating)
            //for displaing value as numbers when textStyle is null
            defConverter = getConverter();
            setConverter( newConverter );
            textStyleProperty().addListener( 
                    ( observable, oldOne, newOne ) -> {
                        oldTextStyle = oldOne;
                        setText( getText() );
                        oldTextStyle = null;
                        updateColumns();
                    }
            );
        }
        
        private void updateColumns() {
            if( getTextStyle() == null ) {
                setColumnCount( getHighLimit().toString().length() );
                return;
            }
            int l = 0;
            for( Month m : Month.values() ) {                
                int ll = m.getDisplayName( getTextStyle(), getTextLocale() ).length(); 
                if( l < ll ) l = ll;
            }
            setColumnCount( l );
        }        
        
        private final ObjectProperty<TextStyle> textStyle = 
                new SimpleObjectProperty<>(this, "textStyle");
        public final ObjectProperty<TextStyle> textStyleProperty() {
            return textStyle;
        }
        public final void setTextStyle( TextStyle style ) {
            textStyleProperty().setValue( style );
        }
        public final TextStyle getTextStyle() {
            return textStyleProperty().getValue();
        }
        
        private final ObjectProperty<Locale> textLocale = 
                new SimpleObjectProperty<>(this, "textLocale");
        public final ObjectProperty<Locale> textLocaleProperty() {
            return textLocale;
        }
        public final void setTextLocale( Locale locale ) {
            textLocaleProperty().setValue( locale );
        }
        public final Locale getTextLocale() {
            Locale value = textLocaleProperty().getValue();
            if( value == null ) return DEFAULT_LOCALE;
            return value;
        }    
        final Locale DEFAULT_LOCALE = Locale.getDefault();
        
        
    }
    
}
