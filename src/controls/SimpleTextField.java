package controls;

import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 *
 * @author P5
 */
public class SimpleTextField extends Control {
      
    protected static class Content implements ObservableStringValue {
        private String value;
        private ExpressionHelper<String> helper = null;
        
        @Override
        public String get() {
            return value;
        }

        @Override
        public void addListener( ChangeListener<? super String> listener ) {
            helper = ExpressionHelper.addListener( helper, this, listener );
        }

        @Override
        public void removeListener( ChangeListener<? super String> listener ) {
            helper = ExpressionHelper.removeListener( helper, listener );
        }

        @Override
        public String getValue() {
            return get();
        }

        @Override
        public void addListener( InvalidationListener listener ) {
            helper = ExpressionHelper.addListener( helper, this, listener );
        }

        @Override
        public void removeListener( InvalidationListener listener ) {
            helper = ExpressionHelper.removeListener( helper, listener );
        }
        
        public Integer getIntegerValue() {
            return Integer.valueOf( get() );
        }
        
        public void setValue( String value, boolean notify ) {
            if( this.value != null && this.value.equals( value ) ) return;
            this.value = value;
            if( notify )
                ExpressionHelper.fireValueChangedEvent( helper );
        }
                
    }
    
    private final Content content = new Content();;
    public Content getContent() {
        return content;
    }
    
    public final SimpleIntegerProperty highLimit = new SimpleIntegerProperty( Integer.MAX_VALUE ) {
        @Override
        public void set( int newValue ) {
            super.set( newValue );
        }

        @Override
        protected void invalidated() {
            super.invalidated();
            int value = get();
            setColumnCount( Integer.toString( value ).length() );
        }
        
    };
    public void setHighLimit( Integer limit ) { highLimit.set( limit ); }
    public Integer getHighLimit() { return highLimit.get(); }
    
    public final SimpleIntegerProperty columnCount = new SimpleIntegerProperty();
    public void setColumnCount( Integer count ) { columnCount.setValue( count ); }
    public Integer getColumnCount() { return columnCount.getValue(); }
    
    @Override
    protected Skin<?> createDefaultSkin() {
        return new SimpleSkin(this, new SimpleBehavior(this) );
    }

    public SimpleTextField() {
        getStyleClass().add("text-input");
    }
        
}