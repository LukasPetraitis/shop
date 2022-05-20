package com.visma.shop.warehouse.exception;

public class NotEnoughInStorageException extends Exception{
    public NotEnoughInStorageException() {
        super("Not enough items in storage");
    }
}
