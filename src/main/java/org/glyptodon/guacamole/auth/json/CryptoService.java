/*
 * Copyright (C) 2015 Glyptodon LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.glyptodon.guacamole.auth.json;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.GuacamoleServerException;

/**
 * Service for handling cryptography-related operations, such as decrypting
 * encrypted data.
 *
 * @author Michael Jumper
 */
public class CryptoService {

    /**
     * The name of the cipher transformation that should be used to decrypt any
     * String provided to decrypt().
     */
    private static final String DECRYPTION_CIPHER_NAME = "AES/CBC/PKCS5Padding";

    /**
     * IV which is all null bytes (all binary zeroes). Usually, using a null IV
     * is a horrible idea. As our plaintext will always be prepended with the
     * HMAC signature of the rest of the message, we are effectively using the
     * HMAC signature itself as the IV. For our purposes, where the encrypted
     * value becomes an authentication token, this is OK.
     */
    private static final IvParameterSpec NULL_IV = new IvParameterSpec(new byte[] {
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0
    });

    /**
     * Decrypts the given ciphertext using the provided key, returning the
     * resulting plaintext. If any error occurs during decryption at all, a
     * GuacamoleException is thrown. The IV used for the decryption process is
     * a null IV (all binary zeroes).
     *
     * @param key
     *     The key to use to decrypt the provided ciphertext.
     *
     * @param cipherText
     *     The ciphertext to decrypt.
     *
     * @return
     *     The plaintext which results from decrypting the ciphertext with the
     *     provided key.
     *
     * @throws GuacamoleException
     *     If any error at all occurs during decryption.
     */
    public byte[] decrypt(Key key, byte[] cipherText) throws GuacamoleException {

        try {

            // Init cipher for descryption using secret key
            Cipher cipher = Cipher.getInstance(DECRYPTION_CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, key, NULL_IV);

            // Perform decryption
            return cipher.doFinal(cipherText);

        }

        // Rethrow all decryption failures identically
        catch (InvalidAlgorithmParameterException e) {
            throw new GuacamoleServerException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new GuacamoleServerException(e);
        }
        catch (NoSuchPaddingException e) {
            throw new GuacamoleServerException(e);
        }
        catch (InvalidKeyException e) {
            throw new GuacamoleServerException(e);
        }
        catch (IllegalBlockSizeException e) {
            throw new GuacamoleServerException(e);
        }
        catch (BadPaddingException e) {
            throw new GuacamoleServerException(e);
        }

    }

}