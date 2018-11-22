package controls;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author 
 */
public class DateTimeFieldBehavior<C extends DateTimeField> extends BehaviorBase<C>{
    
    protected static final ArrayList<KeyBinding> BINDINGS = new ArrayList<>();
    
    public DateTimeFieldBehavior( C control ) {
        super( control, BINDINGS );
    }

    @Override
    public void mousePressed( MouseEvent e ) {
        super.mousePressed( e );
        if( skin != null ) {
            skin.receiveMousePressed( e );
        }
    }

    @Override
    protected void focusChanged() {
        super.focusChanged();
        if( !getControl().isFocused() && skin != null ) {            
            skin.updateControlValue();
            System.out.println( "DateTimeFieldBehavior.focusChanged" );
        }
    }
    
    private DateTimeFieldSkin skin;
    public void setSkin( final DateTimeFieldSkin skin ) { this.skin = skin; }

    
}
