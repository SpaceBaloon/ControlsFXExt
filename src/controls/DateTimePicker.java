package controls;

import java.time.LocalDate;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Skin;

/**
 *
 */
public class DateTimePicker extends DatePicker {

    public DateTimePicker() {
        this(null);
    }

    public DateTimePicker( final LocalDate localDate ) {
        super( localDate );
        setFocusTraversable( false );        
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        DateTimePickerSkin skin = new DateTimePickerSkin( this );
        /**
         * This is for the sake of traversing through the TextFields.
         * By default traverse turned off in ComboBoxPopupControl constructor.
         */
        setImpl_traversalEngine( null );
        return skin;
    }

    void setFakeFocus( final boolean value ) {
        setFocused( value );
    }
    
}
