package controls;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;

/**
 *
 * @author 
 */
public class QuickDatePickerPopupContent extends Region {

    public QuickDatePickerPopupContent() {
        setBackground( 
                new Background(
                        new BackgroundFill( Paint.valueOf( "magenta" ), null, null )
                )
        );
    }    

    @Override
    protected double computePrefHeight( double width ) {
        return 360;
    }

    @Override
    protected double computePrefWidth( double height ) {
        return 240;
    }
    
}
