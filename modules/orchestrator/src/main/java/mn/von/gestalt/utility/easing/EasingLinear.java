package mn.von.gestalt.utility.easing;

public class EasingLinear {

    public static float easeNone (float t,float b , float c, float d) {
        return c*t/d + b;
    }

    public static float easeIn (float t,float b , float c, float d) {
        return c*t/d + b;
    }

    public static float easeOut (float t,float b , float c, float d) {
        return c*t/d + b;
    }

    public static float easeInOut (float t,float b , float c, float d) {
        return c*t/d + b;
    }

}
