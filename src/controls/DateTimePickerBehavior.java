package controls;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 */
public class DateTimePickerBehavior extends ComboBoxBaseBehavior<LocalDate>{
    
    public DateTimePickerBehavior( final DateTimePicker control ) {
        super( control, new ArrayList<KeyBinding>() );
    }
    
}
