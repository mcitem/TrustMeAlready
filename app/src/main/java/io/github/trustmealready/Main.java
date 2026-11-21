package io.github.trustmealready;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.libxposed.api.XposedModule;

public class Main extends XposedModule {

    private static final String TAG = "TrustMeAlready";
    private static final String SSL_CLASS_NAME = "com.android.org.conscrypt.TrustManagerImpl";
    private static final String SSL_METHOD_NAME = "checkTrustedRecursive";
    private static final Class<?> SSL_RETURN_TYPE = List.class;
    private static final Class<?> SSL_RETURN_PARAM_TYPE = X509Certificate.class;

    @Override
    @SuppressLint("PrivateApi")
    public void onModuleLoaded(@NonNull ModuleLoadedParam param) {
        log(Log.INFO, TAG, "TrustMeAlready loading in process: " + param.getProcessName());
        int hookedMethods = 0;

        Class<?> trustManagerClass;
        try {
            trustManagerClass = Class.forName(SSL_CLASS_NAME, false, null);
        } catch (ClassNotFoundException e) {
            log(Log.ERROR, TAG, "Failed to find class: " + SSL_CLASS_NAME, e);
            return;
        }

        for (Method method : trustManagerClass.getDeclaredMethods()) {
            if (!checkSSLMethod(method)) {
                continue;
            }

            log(Log.INFO, TAG, "Hooking method: " + method);
            hook(method).intercept(chain -> new ArrayList<X509Certificate>());
            hookedMethods++;
        }

        log(Log.INFO, TAG, String.format(Locale.ENGLISH, "TrustMeAlready loaded! Hooked %d methods", hookedMethods));
    }

    private boolean checkSSLMethod(Method method) {
        if (!method.getName().equals(SSL_METHOD_NAME)) {
            return false;
        }

        // check return type
        if (!SSL_RETURN_TYPE.isAssignableFrom(method.getReturnType())) {
            return false;
        }

        // check if parameterized return type
        Type returnType = method.getGenericReturnType();
        if (!(returnType instanceof ParameterizedType)) {
            return false;
        }

        // check parameter type
        Type[] args = ((ParameterizedType) returnType).getActualTypeArguments();
        if (args.length != 1 || !(args[0].equals(SSL_RETURN_PARAM_TYPE))) {
            return false;
        }

        return true;
    }
}
