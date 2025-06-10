package com.edwin.rrhh_api.common;

import java.security.SecureRandom;

/**
 * Define funciones para la generación de contraseñas.
 */
public class PasswordGenerator {
    private static final SecureRandom random = new SecureRandom();

    /**
     * Genera una contraseña hexadecimal aleatoria.
     *
     * @param byteLength longitud de la contraseña en bytes
     * @return la contraseña hexadecimal generado
     */
    public static String generateHexPassword(int byteLength) {
        byte[] bytes = new byte[byteLength];
        random.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b)); // 2 dígitos hexadecimales por byte
        }
        return sb.toString();
    }
}
