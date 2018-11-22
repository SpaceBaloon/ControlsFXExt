package controls;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;

/**
 *
 * @author 
 */
public class QuickDatePickerBehavior extends ComboBoxBaseBehavior<DateRange> {

    public QuickDatePickerBehavior( final QuickDatePicker picker ) {
        super( picker, COMBO_BOX_BASE_BINDINGS );
    }

    @Override
    public void onAutoHide() {
        QuickDatePicker picker = ( QuickDatePicker ) getControl();
        QuickDatePickerSkin skin = ( QuickDatePickerSkin ) picker.getSkin();
        skin.updatePopupHide();
        if( !getControl().isShowing() ) super.onAutoHide(); 
    }
    
}
