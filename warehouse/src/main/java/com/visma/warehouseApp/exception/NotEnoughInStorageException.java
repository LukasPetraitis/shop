package com.visma.warehouseApp.exception;

public class NotEnoughInStorageException extends Exception{
    public NotEnoughInStorageException() {
        super("Not enough items in storage");
    }
}
