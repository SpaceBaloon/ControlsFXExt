package controls;

import java.time.Month;
import java.time.chrono.Chronology;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

/**
 *
 */
public class CaptionNumericTextField extends NumericTextField {

    private StringConverter<Integer> valueConverter = new StringConverter<Integer>() {
        @Override
        public String toString( Integer object ) {
//            System.out.println( "toString: " + object );
            if( object != null && object > 0 ) 
                return Month.of( object ).getDisplayName( TextStyle.FULL, Locale.getDefault() );
            return "";
        }

        @Override
        public Integer fromString( String string ) {
//            System.out.println( "fromString: " + conv.toString( LocalDate.now() ) );
            if( string == null || "".equals( string.trim() ) ) return null;
            if( string.chars().allMatch( Character::isDigit  ) )
                return Integer.valueOf( string );
            for( Month month : Month.values() )
                if( month.getDisplayName( TextStyle.FULL, Locale.getDefault() ).equals( string ) ) {
                    return month.getValue();
                }
            return null;
        }
    };
    
    public 
    
    LocalDateStringConverter conv = new LocalDateStringConverter(
            FormatStyle.LONG, Locale.getDefault(), Chronology.ofLocale( Locale.getDefault() ) );
    
    public CaptionNumericTextField() {
        this(null);
    }

    public CaptionNumericTextField( Integer value ) {
        this( value, value );
    }

    public CaptionNumericTextField( Integer value, Integer limit ) {
        super( value, limit );
        setConverter( valueConverter );
    }
    
}
