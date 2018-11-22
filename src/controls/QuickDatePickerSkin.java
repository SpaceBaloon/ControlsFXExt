package controls;

import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;


/**
 *
 */
public class QuickDatePickerSkin extends ComboBoxPopupControl<DateRange>{
    
    private final TextField displayNode;
    private QuickDatePickerPopupContent popupContent;   
    private DateRangeConverter converter = new DateRangeConverter();
    
    public QuickDatePickerSkin( final QuickDatePicker picker  ) {
        super( picker, new QuickDatePickerBehavior( picker ) );
        
        displayNode = getEditableInputNode();
        displayNode.setFocusTraversable( false );
        displayNode.setManaged( false );
    }

    @Override
    public Node getDisplayNode() {
        return displayNode;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    protected Node getPopupContent() {
        if( popupContent == null) {
            popupContent = new QuickDatePickerPopupContent();
        }
        return popupContent;
    }

    @Override
    protected TextField getEditor() {
        return ( ( QuickDatePicker ) getSkinnable() ).getEditor();
    }

    @Override
    protected StringConverter<DateRange> getConverter() {
        return converter;
    } 
    
    public void updatePopupHide() {
        if (!getPopup().isShowing() && getSkinnable().isShowing()) {
            getSkinnable().hide();
        }
    }
    
    protected class DateRangeConverter extends StringConverter<DateRange> {

        @Override
        public String toString( DateRange object ) {
            return object.toString();
        }

        @Override
        public DateRange fromString( String string ) {
            return DateRange.fromString(string);
        }
        
    }
    
}
