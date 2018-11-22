package controls;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;

/**
 * ISSUE: value properties don't invalidate properly (see dateValue property).
 *
 */
public class DateTimeField extends Control {
    
    public static enum ValueKind {
        DATE, DATE_TIME, TIME;
    }
    
    /**
     * Determine type through constructor.
     * Default is Date.
     */
    public DateTimeField() {
        this( LocalDate.now() );
        kind.setValue(ValueKind.DATE );
    }
    
    public DateTimeField( LocalDate date ) {        
        this( date.atTime( LocalTime.MIDNIGHT ) );
        kind.setValue( ValueKind.DATE );
    }
    
    public DateTimeField( LocalTime time ) {
        this( time.atDate( LocalDate.now() ) );
        kind.setValue( ValueKind.TIME );
    }
    
    public DateTimeField( LocalDateTime date ) { 
        setDateTimeValue( date );
        setFocusTraversable( false );
        getStyleClass().addAll( "text-input", "text-field" );
        kind.setValue( ValueKind.DATE_TIME );
    }    

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DateTimeFieldSkin( this );
    }
    
    private ObjectProperty<Font> font = new ObjectPropertyBase<Font>( Font.getDefault() ) {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "font";
        }
    };
    
    public ObjectProperty<Font> getFontProperty() { return font; }
    public final void setFont(Font value) { font.setValue(value); }
    public final Font getFont() { return font.getValue(); }
     
    /**
     * We can assign value separetely.
     */
    private boolean internalSet = false;
    private ObjectProperty<LocalDateTime> dateTimeValue = new SimpleObjectProperty<LocalDateTime>(this, "dateTimeValue") {
        @Override
        protected void invalidated() {
            getValue();            
            super.invalidated();
            if( internalSet ) return;
            internalSet = true;
            try {
                LocalDateTime value = getValue();
                LocalDate d = null; 
                LocalTime t = null;
                if( value != null ) {
                    d = value.toLocalDate(); t = value.toLocalTime();
                }
                setDateValue( d );
                setTimeValue( t );
            } finally {
                internalSet = false;
            }
        }
    };
    final public void setDateTimeValue( final LocalDateTime newValue ) { dateTimeValue.setValue( newValue ); }
    public LocalDateTime getDateTimeValue() { return dateTimeValue.getValue(); }
    public ObjectProperty<LocalDateTime> dateTimeValueProperty() { return dateTimeValue; }
    
    private ObjectProperty<LocalDate> dateValue = new SimpleObjectProperty<LocalDate>( this, "dateValue" ) {
        @Override
        protected void invalidated() {
            getValue();//that's very strange but without this call it doesn't work
            super.invalidated();
            if( internalSet ) return;
            internalSet = true;
            try {
                LocalDate value = getValue();
                LocalTime t = getTimeValue();
                setDateTimeValue( value == null ? null : 
                        LocalDateTime.of( value,
                        t == null ? LocalTime.MIDNIGHT : t
                ) );
            } finally {
                internalSet = false;
            }
        }        
    };
    public void setDateValue( final LocalDate newValue ) { dateValue.setValue( newValue ); }
    public LocalDate getDateValue() { return dateValue.getValue(); }
    public ObjectProperty<LocalDate> dateValueProperty() { return dateValue; }
    
    private ObjectProperty<LocalTime> timeValue = new SimpleObjectProperty<LocalTime>( this, "timeValue" ) {
        @Override
        protected void invalidated() {
            getValue();
            super.invalidated();
            if( internalSet ) return;
            internalSet = true;
            try {
                LocalTime value = getValue();
                LocalDate d = getDateValue();
                setDateTimeValue( value == null ? null :
                        LocalDateTime.of( d == null ? LocalDate.MIN : d, value
                ) );
            } finally {
                internalSet = false;
            }
        }         
    };
    public void setTimeValue( final LocalTime newValue ) { timeValue.setValue( newValue ); }
    public LocalTime getTimeValue() { return timeValue.getValue(); }
    public ObjectProperty<LocalTime> timeValueProperty() { return timeValue; }
    
    /**
     * Kind property has meaning only to display skin TextFields.
     */
    private ObjectProperty<ValueKind> kind = new SimpleObjectProperty<ValueKind>(this, "kind") {
        @Override
        protected void invalidated() {
            super.invalidated();
            final ValueKind value = getValue();
            timeKind.setValue(ValueKind.DATE_TIME.equals( value ) 
                    || ValueKind.TIME.equals( value ) );
            dateKind.setValue( ValueKind.DATE_TIME.equals( value ) 
                    || ValueKind.DATE.equals( value ) );
            requestLayout();
        }
    };
    public ValueKind getKind() { return kind.getValue(); }
    
    private BooleanProperty dateKind = new SimpleBooleanProperty( this, "dateKind" );
    public boolean isDate() { return dateKind.getValue(); }
    protected BooleanProperty dateKindProperty() { return dateKind; }
    
    private BooleanProperty timeKind = new SimpleBooleanProperty( this, "timeKind" );
    public boolean isTime() { return timeKind.getValue(); }
    protected BooleanProperty timeKindProperty() { return timeKind; }
    
    public void setFakeFocus( boolean value ) {
        setFocused( value );
    }
    
    private ObjectProperty<TextStyle> monthStyle = 
            new SimpleObjectProperty<>(this, "monthStyle");
    public final ObjectProperty<TextStyle> monthStyleProperty() {
        return monthStyle;
    }
    public final void setMonthStyle( TextStyle style ) {
        monthStyleProperty().setValue( style );
    }
    public final TextStyle getMonthStyle() {
        return monthStyleProperty().getValue();
    }
    
}
