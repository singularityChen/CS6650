package org.example;

public class Swipe {
    String swiper;
    String swipee;
    String leftOrRight;

    public Swipe(String swiper, String swipee, String leftOrRight){
        this.swipee = swipee;
        this.swiper = swiper;
        this.leftOrRight = leftOrRight;
    }

    public String getSwipee() {
        return swipee;
    }

    public String getSwiper(){
        return swiper;
    }

    public String getLeftOrRight(){
        return leftOrRight;
    }
}
