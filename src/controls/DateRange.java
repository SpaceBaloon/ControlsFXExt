package controls;

import java.time.LocalDate;

/**
 *
 * @author 
 */
public class DateRange {
    
    public static final String DELIMITER = "/";
    
    private LocalDate fromDate;
    private LocalDate toDate;

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public DateRange() {
        this( LocalDate.now(), LocalDate.now() );
    }

    public DateRange( LocalDate toDate ) {
        this(toDate, toDate);
    }

    public DateRange( LocalDate fromDate, LocalDate toDate ) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    
    public static DateRange fromString(final String value) {
        if("".equals( value )) return new DateRange();
        String[] vars = value.split( DELIMITER );
        if(vars.length == 1) 
            return new DateRange( LocalDate.parse( vars[0] ) );
        else
            return new DateRange( LocalDate.parse( vars[0] ),
                    LocalDate.parse( vars[1] )
            );
    }

    @Override
    public String toString() {
        return fromDate.toString() + " " + DELIMITER + " " + toDate.toString();
    }
    
}
