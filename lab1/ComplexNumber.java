import java.lang.Math;

public class ComplexNumber {
    private double real;
    private double imag;

    public ComplexNumber(double re, double im) {
        this.real = re;
        this.imag = im;
    }

    public double getReal() {
        return this.real;
    }

    public double getImag() {
        return this.imag;
    }

    public ComplexNumber add(ComplexNumber number){
        double newReal = this.real+number.real;
        double newImag = this.imag+number.imag;

        return new ComplexNumber(newReal, newImag);
    }

    public ComplexNumber multiply(ComplexNumber number){
        double newReal = (this.real * number.real) - (this.imag * number.imag);
        double newImag = (this.real * number.imag) + (this.imag * number.real);

        ComplexNumber newComplexNumber = new ComplexNumber(newReal, newImag);
        return newComplexNumber;
    }

    public double absolute() {
        return Math.hypot(this.real, this.imag);
    }

}
