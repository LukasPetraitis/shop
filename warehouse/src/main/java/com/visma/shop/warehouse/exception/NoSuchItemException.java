package com.visma.shop.warehouse.exception;

public class NoSuchItemException extends Exception{

    public NoSuchItemException() {
        super("No item with such id");
    }
}
