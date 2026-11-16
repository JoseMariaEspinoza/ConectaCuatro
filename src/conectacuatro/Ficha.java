package conectacuatro;

import java.io.Serializable;

public class Ficha implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private ColorFicha colorFicha;

    public Ficha(ColorFicha colorFicha) {
        this.colorFicha = colorFicha;
    }

    public ColorFicha getColorFicha() {
        return colorFicha;
    }

    public void setColorFicha(ColorFicha colorFicha) {
        this.colorFicha = colorFicha;
    }

    @Override
    public String toString() {
        if (this.colorFicha == ColorFicha.ROJO)
            return Color.ROJO + "O" + Color.RESET;
        else
            return Color.AMARILLO + "O" + Color.RESET;
    }

    
}
