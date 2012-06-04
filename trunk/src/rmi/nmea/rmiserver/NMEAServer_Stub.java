// Stub class generated by rmic, do not edit.
// Contents subject to change without notice.

package rmi.nmea.rmiserver;

public final class NMEAServer_Stub
    extends java.rmi.server.RemoteStub
    implements rmi.nmea.rmiserver.RemoteNMEAInterface, java.rmi.Remote
{
    private static final java.rmi.server.Operation[] operations = {
	new java.rmi.server.Operation("nmea.server.ctx.NMEADataCache getNMEACache()"),
	new java.rmi.server.Operation("void registerForNotification(nmea.client.Notifiable)"),
	new java.rmi.server.Operation("void unregisterForNotification(nmea.client.Notifiable)")
    };
    
    private static final long interfaceHash = 6296721287492850241L;
    
    private static final long serialVersionUID = 2;
    
    private static boolean useNewInvoke;
    private static java.lang.reflect.Method $method_getNMEACache_0;
    private static java.lang.reflect.Method $method_registerForNotification_1;
    private static java.lang.reflect.Method $method_unregisterForNotification_2;
    
    static {
	try {
	    java.rmi.server.RemoteRef.class.getMethod("invoke",
		new java.lang.Class[] {
		    java.rmi.Remote.class,
		    java.lang.reflect.Method.class,
		    java.lang.Object[].class,
		    long.class
		});
	    useNewInvoke = true;
	    $method_getNMEACache_0 = rmi.nmea.rmiserver.RemoteNMEAInterface.class.getMethod("getNMEACache", new java.lang.Class[] {});
	    $method_registerForNotification_1 = rmi.nmea.rmiserver.RemoteNMEAInterface.class.getMethod("registerForNotification", new java.lang.Class[] {rmi.nmea.client.Notifiable.class});
	    $method_unregisterForNotification_2 = rmi.nmea.rmiserver.RemoteNMEAInterface.class.getMethod("unregisterForNotification", new java.lang.Class[] {rmi.nmea.client.Notifiable.class});
	} catch (java.lang.NoSuchMethodException e) {
	    useNewInvoke = false;
	}
    }
    
    // constructors
    public NMEAServer_Stub() {
	super();
    }
    public NMEAServer_Stub(java.rmi.server.RemoteRef ref) {
	super(ref);
    }
    
    // methods from remote interfaces
    
    // implementation of getNMEACache()
    public nmea.server.ctx.NMEADataCache getNMEACache()
	throws java.rmi.RemoteException
    {
	try {
	    if (useNewInvoke) {
		Object $result = ref.invoke(this, $method_getNMEACache_0, null, 3544281633149487421L);
		return ((nmea.server.ctx.NMEADataCache) $result);
	    } else {
		java.rmi.server.RemoteCall call = ref.newCall((java.rmi.server.RemoteObject) this, operations, 0, interfaceHash);
		ref.invoke(call);
		nmea.server.ctx.NMEADataCache $result;
		try {
		    java.io.ObjectInput in = call.getInputStream();
		    $result = (nmea.server.ctx.NMEADataCache) in.readObject();
		} catch (java.io.IOException e) {
		    throw new java.rmi.UnmarshalException("error unmarshalling return", e);
		} catch (java.lang.ClassNotFoundException e) {
		    throw new java.rmi.UnmarshalException("error unmarshalling return", e);
		} finally {
		    ref.done(call);
		}
		return $result;
	    }
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of registerForNotification(Notifiable)
    public void registerForNotification(rmi.nmea.client.Notifiable $param_Notifiable_1)
	throws java.rmi.RemoteException
    {
	try {
	    if (useNewInvoke) {
		ref.invoke(this, $method_registerForNotification_1, new java.lang.Object[] {$param_Notifiable_1}, 5376817839078363496L);
	    } else {
		java.rmi.server.RemoteCall call = ref.newCall((java.rmi.server.RemoteObject) this, operations, 1, interfaceHash);
		try {
		    java.io.ObjectOutput out = call.getOutputStream();
		    out.writeObject($param_Notifiable_1);
		} catch (java.io.IOException e) {
		    throw new java.rmi.MarshalException("error marshalling arguments", e);
		}
		ref.invoke(call);
		ref.done(call);
	    }
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of unregisterForNotification(Notifiable)
    public void unregisterForNotification(rmi.nmea.client.Notifiable $param_Notifiable_1)
	throws java.rmi.RemoteException
    {
	try {
	    if (useNewInvoke) {
		ref.invoke(this, $method_unregisterForNotification_2, new java.lang.Object[] {$param_Notifiable_1}, 2109539391690866604L);
	    } else {
		java.rmi.server.RemoteCall call = ref.newCall((java.rmi.server.RemoteObject) this, operations, 2, interfaceHash);
		try {
		    java.io.ObjectOutput out = call.getOutputStream();
		    out.writeObject($param_Notifiable_1);
		} catch (java.io.IOException e) {
		    throw new java.rmi.MarshalException("error marshalling arguments", e);
		}
		ref.invoke(call);
		ref.done(call);
	    }
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
}