package controls;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Bounds;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;

/**
 *
 * @author 
 * @param <T> 
 * @param <B> 
 */
public class SimpleSkin<T extends SimpleTextField, B extends SimpleBehavior<T>> extends BehaviorSkinBase<T, B> {

    private final Text textNode = new Text();
    private final Path selectionHighlightRect = new Path();
    private final Pane groupPane = new Pane();
    
    private final Border focusedBorder = new Border( new BorderStroke( 
            Color.DODGERBLUE,
            BorderStrokeStyle.SOLID, 
            CornerRadii.EMPTY, 
            BorderWidths.DEFAULT
    ) );
    private final Border baseBorder = new Border( new BorderStroke( 
            Color.GRAY,
            BorderStrokeStyle.SOLID, 
            CornerRadii.EMPTY, 
            BorderWidths.DEFAULT
    ) );
    
    protected final ObservableObjectValue<FontMetrics> fontMetrics;
    
    public SimpleSkin( T control, B behavior ) {
        super( control, behavior );
        textNode.setManaged( false ); 
        textNode.getStyleClass().add("text");
        selectionHighlightRect.setStroke( null );
        selectionHighlightRect.setManaged( false );
        
        groupPane.getChildren().addAll( selectionHighlightRect, textNode );
        groupPane.setBorder( baseBorder );
        getChildren().addAll( groupPane );
        
        getSkinnable().focusedProperty().addListener( ( observable ) -> {
            handleFocus();
        } );
        getSkinnable().getContent().addListener( (observable ) -> {
            textChanged();
        } );
        getSkinnable().columnCount.addListener( (observable ) -> {
            getSkinnable().requestLayout();
        } );
        
        
        fontMetrics = new ObjectBinding<FontMetrics>() {
            { bind( textNode.fontProperty() ); }
            @Override
            protected FontMetrics computeValue() {
                return Toolkit.getToolkit().getFontLoader().getFontMetrics(textNode.getFont());
            }
        };
    }
    
    protected void updateHighlightRect( boolean lightUp ) {
        if( lightUp ) {
            selectionHighlightRect.setFill( Color.DODGERBLUE );
            textNode.setFill( Color.WHITE );
        }
        else {
            selectionHighlightRect.setFill( null );
            textNode.setFill( Color.BLACK );
        }
        selectionHighlightRect.getElements().clear();
        if( lightUp ) {
            Bounds bounds = textNode.getLayoutBounds();
            selectionHighlightRect.getElements().addAll( 
                    new MoveTo( bounds.getMinX(), bounds.getMinY() ),
                    new LineTo( bounds.getMaxX(), bounds.getMinY() ),
                    new LineTo( bounds.getMaxX(), bounds.getMaxY() ),
                    new LineTo( bounds.getMinX(), bounds.getMaxY() ),
                    new LineTo( bounds.getMinX(), bounds.getMinY() )
            );
        }
    }

    @Override
    protected void layoutChildren( double contentX, double contentY, 
            double contentWidth, double contentHeight ) {
        super.layoutChildren( contentX, contentY, contentWidth, contentHeight );
        updateTextPosition();
    }

    @Override
    protected double computeMaxWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
        return computePrefWidth( height, topInset, rightInset, bottomInset, leftInset );
    }

    @Override
    protected double computeMinWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
        return computePrefWidth( height, topInset, rightInset, bottomInset, leftInset );
    }

    @Override
    protected double computePrefWidth( double height, double topInset,
            double rightInset, double bottomInset, double leftInset ) {        
        double w = fontMetrics.get().computeStringWidth( "0" );
        double result = leftInset + rightInset + getSkinnable().getColumnCount() * w;        
        return result;
    }

    @Override
    protected double computePrefHeight( double width, double topInset, 
            double rightInset, double bottomInset, double leftInset ) {        
        return topInset + bottomInset + textNode.getLayoutBounds().getHeight();
    }

    private void handleFocus() {
        updateHighlightRect( getSkinnable().focusedProperty().get() );
    }

    private void textChanged() {
        textNode.setText( getSkinnable().getContent().get() );
        updateTextPosition();
    }
    
    public void updateTextPosition() {        
        Bounds bounds = textNode.getLayoutBounds();
        Bounds groupBounds = groupPane.getLayoutBounds();
        double offset = textNode.getBaselineOffset();
        double textHeight = bounds.getHeight() - offset;
        //center_right alignment
        textNode.setY( ( offset + groupBounds.getHeight() - textHeight ) / 2 );        
        textNode.setX( groupBounds.getMaxX() - bounds.getWidth() );
        updateHighlightRect( getSkinnable().focusedProperty().get() );
    }
    
}
