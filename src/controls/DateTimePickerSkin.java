package controls;

import com.sun.javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * ISSUE: updating value from displayNode to DateTimePicker (see getDisplayNode).
 *
 */
public class DateTimePickerSkin extends DatePickerSkin{

    private DateTimeField displayNode;
    
    public DateTimePickerSkin( final DateTimePicker dateTimePicker ) {
        super( dateTimePicker );
        getSkinnable().focusedProperty().addListener( 
                (observable, tosh, isFocused ) -> {
                    if(!isFocused) {
                        setTextFromTextFieldIntoComboBoxValue();
                        System.out.println( getSkinnable().getValue() );
                    }
                }
        );
    }

    @Override
    public Node getDisplayNode() {
        if(displayNode == null) {
            displayNode = new DateTimeField() {
                /**
                 * For the sake of updating value from displayNode we let DateTimeField
                 * receive focus, so after we lost focus DateTimeField lost focus too,
                 * DateTimeField updates itself and then we can update ourself.
                 * Now it works fine when we lost focus at all, but when we popup
                 * DatePickerContent, value updates but TextFields don't.
                 * (maybe it's becase of DateTimeField ISSUE)
                 * It's defenetly not what we want. DateTimeField is not the owner of 
                 * TextFields that represent value. Probably we need implenting set of 
                 * TextFields as standalone class and give it opportunity to set some 
                 * sort of owner interface that will receive events from TextFields.
                 * @param value 
                 */
                @Override
                public void setFakeFocus( boolean value ) {
                    super.setFakeFocus( value );
                    DateTimePicker dateTimePicker = (DateTimePicker)getSkinnable();
                    dateTimePicker.setFakeFocus( value );
                }
            };
            displayNode.getStyleClass().add( "date-picker-display-node" );
            updateDisplayNode();
        }
        return displayNode;
    }

    @Override
    protected TextField getEditor() {
        return null;
    }

    @Override
    protected void updateDisplayNode() {
        DateTimePicker control = (DateTimePicker)getSkinnable();        
        displayNode.setDateValue( control.getValue() );
    }

    @Override
    protected void setTextFromTextFieldIntoComboBoxValue() {
        getSkinnable().setValue( displayNode.getDateValue() );
    }
    
}
