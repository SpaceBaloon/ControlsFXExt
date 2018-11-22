package controls;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

/**
 *
 * @author 
 */
public class QuickDatePicker extends ComboBoxBase<DateRange> {    
        
    public QuickDatePicker() {
        this( new DateRange() );
    }
    
    public QuickDatePicker( DateRange value ) {
        setValue( value );
        setEditable( true );
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new QuickDatePickerSkin( this );
    }
    
    private TextField editor;
    
    public TextField getEditor() {
        if(editor == null) {
            editor = new ComboBoxListViewSkin.FakeFocusTextField();
            editor.textProperty().addListener( 
                    ( observable, oldValue, newValue ) -> {
                        System.out.println( "Old value: " + oldValue + ", new value: " + newValue );
                    }
            );
        }
        return editor;
    }
    
}
