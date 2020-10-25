package com.example.covivid.Retrofit.Exceptions;

import java.io.IOException;

public class NoInternetConnectionException extends IOException
{
    public NoInternetConnectionException(String message)
    {
        super(message);
    }
}
