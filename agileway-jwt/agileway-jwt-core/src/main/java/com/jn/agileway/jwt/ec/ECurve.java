package com.jn.agileway.jwt.ec;


import java.io.Serializable;
import java.security.spec.ECParameterSpec;
import java.util.Objects;

/**
 * Cryptographic curve. This class is immutable.
 *
 * <p>Includes constants for the following standard cryptographic curves:
 *
 * <ul>
 *     <li>{@link ECurves#P_256}
 *     <li>{@link ECurves#SECP256K1}
 *     <li>{@link ECurves#P_256K} (Deprecated)
 *     <li>{@link ECurves#P_384}
 *     <li>{@link ECurves#P_521}
 *     <li>{@link ECurves#Ed25519}
 *     <li>{@link ECurves#Ed448}
 *     <li>{@link ECurves#X25519}
 *     <li>{@link ECurves#X448}
 * </ul>
 *
 * <p>See
 *
 * <ul>
 *     <li>"Digital Signature Standard (DSS)", FIPS PUB 186-3, June 2009,
 *         National Institute of Standards and Technology (NIST).
 *     <li>CFRG Elliptic Curve Diffie-Hellman (ECDH) and Signatures in JSON
 *         Object Signing and Encryption (JOSE) (RFC 8037).
 * </ul>
 *
 */
public final class ECurve implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * The JOSE curve name.
     */
    private final String name;
    /**
     * The standard curve name, {@code null} if not specified.
     */
    private final String stdName;
    /**
     * The standard object identifier for the curve, {@code null}
     * if not specified.
     */
    private final String oid;


    /**
     * Creates a new cryptographic curve with the specified JOSE name. A
     * standard curve name and object identifier (OID) are not unspecified.
     *
     * @param name The JOSE name of the cryptographic curve. Must not be
     *             {@code null}.
     */
    public ECurve(final String name) {
        this(name, null, null);
    }


    /**
     * Creates a new cryptographic curve with the specified JOSE name,
     * standard name and object identifier (OID).
     *
     * @param name    The JOSE name of the cryptographic curve. Must not
     *                be {@code null}.
     * @param stdName The standard name of the cryptographic curve,
     *                {@code null} if not specified.
     * @param oid     The object identifier (OID) of the cryptographic
     *                curve, {@code null} if not specified.
     */
    public ECurve(final String name, final String stdName, final String oid) {
        if (name == null) {
            throw new IllegalArgumentException("The JOSE cryptographic curve name must not be null");
        }
        this.name = name;
        this.stdName = stdName;
        this.oid = oid;
    }




    /**
     * Returns the JOSE name of this cryptographic curve.
     *
     * @return The JOSE name.
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the standard name of this cryptographic curve.
     *
     * @return The standard name, {@code null} if not specified.
     */
    public String getStdName() {
        return stdName;
    }


    /**
     * Returns the standard object identifier (OID) of this cryptographic
     * curve.
     *
     * @return The OID, {@code null} if not specified.
     */
    public String getOID() {
        return oid;
    }


    /**
     * Returns the parameter specification for this cryptographic curve.
     *
     * @return The EC parameter specification, {@code null} if it cannot be
     *         determined.
     */
    public ECParameterSpec toECParameterSpec() {
        return ECurveParameterTable.get(this);
    }


    /**
     * @see #getName
     */
    @Override
    public String toString() {
        return getName();
    }


    @Override
    public boolean equals(final Object object) {
        return object instanceof ECurve && this.toString().equals(object.toString());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

}