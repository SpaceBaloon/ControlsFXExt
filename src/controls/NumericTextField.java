package controls;

import com.sun.javafx.scene.control.skin.TextFieldSkin;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

/**
 *
 */
public class NumericTextField extends TextField {
    
    private final int LOW_LIMIT = 0;
    private final int HIGH_LIMIT = Integer.MAX_VALUE;
    private boolean replaceAll = false;
   
    private ObjectProperty<StringConverter<Integer>> converter = 
            new SimpleObjectProperty<>(this, "converter" );
    public final ObjectProperty<StringConverter<Integer>> converterProperty() {
        return converter;
    }
    public final StringConverter<Integer> getConverter() {
        StringConverter<Integer> conv = converterProperty().getValue();
        if( conv == null ) {
            return defaultConverter;
        }
        return conv;
    }
    public final void setConverter( StringConverter<Integer> converter ) {
        converterProperty().setValue( converter );
    }
    private StringConverter<Integer> defaultConverter = new StringConverter<Integer>() {
        @Override
        public String toString( Integer object ) {
            String result = "";
            if( object != null ) {
                result = object.toString();
                int min = result.length();
                Integer max = getColumnCount();
                if( max != null )
                    for( int i = min; i< max; i++ ) {
                        result = "0" + result;
                    }
            }
            return result;
        }

        @Override
        public Integer fromString( String string ) {
            Integer result = null;
            try {
                result = Integer.valueOf( string );
            } catch( NumberFormatException ex ) {
                
            }
            return result;
        }
    };
    
    private ObjectProperty<UnaryOperator<TextFormatter.Change>> filter = 
            new SimpleObjectProperty<>(this, "converter" );
    public final ObjectProperty<UnaryOperator<TextFormatter.Change>> filterProperty() {
        return filter;
    }
    public final UnaryOperator<TextFormatter.Change> getFilter() {
        UnaryOperator<TextFormatter.Change> filter = filterProperty().getValue();
        if( filter == null ) {
            return defailtFilter;
        }
        return filter;
    }
    public final void setFilter( UnaryOperator<TextFormatter.Change> filter ) {
        filterProperty().setValue( filter );
    }
    private UnaryOperator<TextFormatter.Change> defailtFilter = new UnaryOperator<TextFormatter.Change>() {
        
        /**
         * This formatter doesn't provide way to delete characters by one.
         * Text is supposed to be always selected, but there are some glithes 
         * becasuse of underlying behaviour of TextField.
         * 
         * @param change
         * @return 
         */
        @Override
        public TextFormatter.Change apply( TextFormatter.Change change ) {
            //we want to shure that text was only deleted and not replaced            
            if( change.isDeleted() && !change.isReplaced() ) return change;
            //for cut and paste
            String newText = change.getControlNewText();
            if( change.isAdded() )
                newText = change.getText();
            final String oldText = change.getControlText();
            /**
             * TODO: eliminate call to converter here, retrieve value directly 
             * from value property ( implement value property for this control ).
             */
            //value without leading symbols            
            final Integer oldValue = getConverter().fromString( oldText );            
            //as we add leading symbols(zero for instance) we need to be sure 
            //that we can add one more character
            //e.g oldText = "0023", so oldValue = "23", suppose maxValue = 2999,
            //so columns count = 4, and we can add another 2 character
            boolean columnsCountExeeded = oldValue != null
                    && getColumnCount() <= oldValue.toString().length();
            //if replaced by only one character we consider this as addition at the end of 
            //the old text
            if( ( change.isReplaced() || change.isAdded() )
                    && newText /*change.getText()*/.length() == 1//one character
                    && !replaceAll//flag for shortcut
                    && !columnsCountExeeded
                    ) {
                newText = oldValue == null ? "" : oldValue.toString()
                        /*oldText*/ + newText;//change.getText();
            }
            final Integer newValue = getConverter().fromString( newText );
            final Integer addedValue = getConverter().fromString( change.getText() );
            if( newValue != null && LOW_LIMIT <= newValue && newValue <= getHighLimit() ) {
                //it's OK, new value has successfully fitted
                change.setText( getConverter().toString( newValue ) );
                change.setRange( 0, change.getControlText().length() ); 
            } else if( addedValue != null && LOW_LIMIT <= addedValue && addedValue <= getHighLimit() ) {
                //second chance: save user input if fits 
                change.setText( getConverter().toString( addedValue ) );
                change.setRange( 0, change.getControlText().length() ); 
            } else {
                //eliminate all unsuited text 
                change.setText( "" );
                change.setRange( 0, 0 );
            }
            //finally select all text
            change.setCaretPosition( change.getControlNewText().length() );
            change.setAnchor( 0 );
            return change;
        }
    };
    
    /**
     * Simple constructor with no value.
     */
    public NumericTextField() {
        this( null );
    }

    /**
     * Value will be used as limit as well.
     * @param value 
     */
    public NumericTextField( Integer value ) {
        this( value, value );
    }
    
    public NumericTextField( Integer value, Integer limit ) {
        super();
        textFormatterProperty().set( new TextFormatter<>( getConverter(), null, getFilter()) );        
        setAlignment( Pos.CENTER_RIGHT );
        getStyleClass().clear();//removeAll( "text-input", "text-field" );
        setHighLimit( limit );
        setText( getConverter().toString( value ) );
        
        converterProperty().addListener( 
                ( observable, oldConveter, newOne ) -> {
                    textFormatterProperty().setValue( new TextFormatter<>( newOne, null, getFilter() ) );
                    Integer v = null;
                    if( oldConveter != null ) {
                        v = oldConveter.fromString( getText() );
                    }
                    if( newOne != null ) {
                        setText( newOne.toString( v ) );
                    }
                } 
        );
        filterProperty().addListener( ( observable, oldOne, newOne ) -> { 
            textFormatterProperty().setValue( new TextFormatter<>( getConverter(), null, newOne ) );
            setText( getText() );
        } );
        
    } 
    
    public final SimpleIntegerProperty highLimit = new SimpleIntegerProperty() {
        @Override
        protected void invalidated() {            
            super.invalidated();
            setColumnCount( Integer.toString( get() ).length() );
        }        
    };    
    public final void setHighLimit( Integer limit ) { 
        if( limit == null ) limit = HIGH_LIMIT;
        highLimit.set( limit ); 
    }
    public Integer getHighLimit() { return highLimit.get(); }
    
    protected final SimpleIntegerProperty columnCount = new SimpleIntegerProperty();
    protected void setColumnCount( Integer count ) {
        if( !Objects.equals( count, getColumnCount() ) ) {            
            columnCount.setValue( count );
            requestLayout();
        }
    }
    protected Integer getColumnCount() { return columnCount.getValue(); }

    /**
     * This is for arrow up and down to increase or decrease value.
     */
    @Override
    public void home() {
        Integer value = getConverter().fromString( getText() );
        if( value == null ) value = 0;
        value++;
        replaceAll = true;
        try {
            setText( getConverter().toString( value ) );
        } finally {
            replaceAll = false;
        }
        selectAll();
    }

    @Override
    public void end() {
        Integer value = getConverter().fromString( getText() );
        if( value != null && value > 0 ) {
            value--;
            replaceAll = true;
            try {
                setText( getConverter().toString( value ) );
            } finally {
                replaceAll = false;
            }
        }
        selectAll();
    }

    @Override
    public void backward() {
        
    }

    @Override
    public void forward() {
        
    }
    
    
    @Override
    protected Skin<?> createDefaultSkin() {
        return new TextFieldSkin( this ) {
            @Override
            protected double computePrefHeight( double width, double topInset, double rightInset, double bottomInset, double leftInset ) {
                double value = super.computePrefHeight( width, topInset, rightInset, bottomInset, leftInset );                
                return value;
            }
            
            @Override
            protected double computePrefWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
                double w = fontMetrics.get().computeStringWidth( "0" );
                return leftInset + rightInset + w * getColumnCount();
            }            
        };
    }
    
    public void setValue( final Integer newValue ) {
        TextFormatter<Integer> textFormatter = ( TextFormatter<Integer> ) getTextFormatter();
        if( textFormatter != null )
            textFormatter.setValue( newValue );
    }
    /**
     * TextFormatter can't be used here for purpose of retrieving value 
     * when owner control lost focus (see DateTimeFieldSkin.updateControlValue), 
     * because getTextFormatter().getValue() doesn't return current value, 
     * it always returns previous one instead.
     * @return Integer - can be null.
     */
    public Integer getValue() { 
        //return ( Integer ) getTextFormatter().getValue();
        return getConverter().fromString( getText() );
    }
    /**
     * @deprecated - Because of above mentioned issue we need to implement new valueProperty.
     * @return 
     */
    @Deprecated
    public ObjectProperty valueProperty() { return getTextFormatter().valueProperty(); };
    
}
