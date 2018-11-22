package controls;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import java.util.ArrayList;
import java.util.List;
import static javafx.scene.input.KeyCode.*;
import javafx.scene.input.KeyEvent;
import static javafx.scene.input.KeyEvent.*;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author 
 * @param <T> 
 */
public class SimpleBehavior<T extends SimpleTextField> extends BehaviorBase<T> {
    
    private SimpleTextField simpleTextControl;
    protected static final List<KeyBinding> KEY_BINDINGS = new ArrayList();
    static {
        KEY_BINDINGS.add(new KeyBinding(null, KEY_TYPED, "InputCharacter")
                .alt(OptionalBoolean.ANY)
                .shift(OptionalBoolean.ANY)
                .ctrl(OptionalBoolean.ANY)
                .meta(OptionalBoolean.ANY));
        KEY_BINDINGS.add(new KeyBinding(BACK_SPACE, KEY_PRESSED,  "DeletePreviousChar"));
        KEY_BINDINGS.add(new KeyBinding(DELETE, KEY_PRESSED,      "DeleteNextChar"));
        
        KEY_BINDINGS.add(new KeyBinding(UP,   KEY_PRESSED,    "Increase"));
        KEY_BINDINGS.add(new KeyBinding(DOWN, KEY_PRESSED,    "Decrease"));
    }
    
    public SimpleBehavior( T control ) {        
        super( control, KEY_BINDINGS );
        simpleTextControl = getControl();
        simpleTextControl.columnCount.addListener( (observable ) -> {
            truncateValue();
            setContentText();
        } );
    }

    @Override
    public void mousePressed( MouseEvent e ) {
        super.mousePressed( e );
        if( !simpleTextControl.isFocused() ) {
            simpleTextControl.requestFocus();
        }
    }

    private KeyEvent lastEvent;
    
    @Override
    protected void callActionForEvent( KeyEvent e ) {
        lastEvent = e;
        super.callActionForEvent( e );
    }

    @Override
    protected void callAction( String name ) {
        System.out.println( "callAction: " + name );
        if( null != name ) switch( name ) {
            case "InputCharacter":
                processCharacter( lastEvent );
                break;
            case "DeletePreviousChar":
                deletePreviousChar();
                break;
            case "DeleteNextChar":
                deleteNextChar();
                break;
            case "Increase":
                increaseValue();
                break;
            case "Decrease":
                decreaseValue();
                break;
            default:
                super.callAction( name );                
        }
    }

    private void processCharacter( KeyEvent event ) {
        if( simpleTextControl.isDisabled() || event.getCharacter().isEmpty() ) return;
        char character = event.getCharacter().charAt( 0 );
        if( '0' <= character && character <= '9' )
            updateText(character);
    }

    private StringBuffer chars = new StringBuffer();
    protected void updateText( char character ) { 
        chars.append( character );
        limitValue();
        truncateValue();
        setContentText();
    }

    protected void limitValue() {
        Integer v = getIntegerValue();
        if( v > simpleTextControl.getHighLimit() || ( v == 0 && chars.length() > 1 ) )
            chars.deleteCharAt( chars.length() -1 );
        else if( 0 < v && '0' == chars.charAt( 0 ) )
            chars.deleteCharAt( 0 );
    }
    
    protected void truncateValue() {
        Integer colCount = simpleTextControl.getColumnCount();
        if( chars.length() > colCount ) {
            Integer v = getIntegerValue();
            if( v > simpleTextControl.getHighLimit() )
                chars.delete( 0, colCount );
            else
                chars.delete( 0, chars.length() - colCount );            
        }
    }

    protected void deleteNextChar() {
        chars.delete( 0, chars.length() );
        setContentText();
    }

    protected void deletePreviousChar() {
        chars.deleteCharAt( chars.length() -1 );
        setContentText();
    }
    
    protected void setContentText() {
        final String value = chars.toString();
        final SimpleTextField.Content content = simpleTextControl.getContent();
        content.setValue( chars.toString(), true );
    }
    
    private Integer getIntegerValue() {
        Integer value = 0;
        try {
            value = Integer.valueOf( chars.toString() );
        } catch( NumberFormatException ex ) {
        }
        return value;
    }

    private void increaseValue() {
        Integer value = getIntegerValue();
        if( value.equals( simpleTextControl.getHighLimit() ) ) return;
        value++;
        insertStringValue( value.toString(), true );
        limitValue();
        truncateValue();
        setContentText();
    }

    private void decreaseValue() {
        Integer value = getIntegerValue();
        if( value == 0 ) return;
        value--;
        insertStringValue( value.toString(), false );
        limitValue();
        truncateValue();
        setContentText();
    }
    
    private void insertStringValue( String value, boolean up ) {
        int valLength = value.length();
        int charLength = chars.length();
        if( ( up && valLength > charLength ) || ( !up && valLength < charLength ) ) {
            chars.delete( 0, charLength );
            chars.append( value );
        } else {
            chars.replace( charLength - valLength, charLength, value );
        }        
    }
    
}
