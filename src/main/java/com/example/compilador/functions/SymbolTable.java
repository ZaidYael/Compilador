package com.example.compilador.functions;

import com.example.compilador.models.HashTable;


import java.io.IOException;
import java.io.RandomAccessFile;

public class SymbolTable {
    private static final String archivo = "tabla_simbolos.dat";
    private static final int recordSize = 100;
    private static final int tableSize = HashTable.size;
    private static boolean inicializado = false;

    public static void guardar(String lexema) throws IOException {
        inicializarArchivoSiEsNecesario();

        try (RandomAccessFile file = new RandomAccessFile(archivo, "rw")) {
            int pos = HashTable.hash(lexema);
            int intentos = 0;

            while (intentos < tableSize) {
                file.seek(pos * recordSize);
                byte[] buffer = new byte[recordSize];
                file.readFully(buffer);
                String existente = new String(buffer).trim();

                if (existente.isEmpty() || existente.equals(lexema)) {
                    file.seek(pos * recordSize);
                    file.writeBytes(String.format("%-" + recordSize + "s", lexema));
                    break;
                }

                pos = (pos + 1) % tableSize;
                intentos++;
            }
        }
    }

    private static void inicializarArchivoSiEsNecesario() throws IOException {
        if (!inicializado) {
            try (RandomAccessFile file = new RandomAccessFile(archivo, "rw")) {
                if (file.length() < (long) tableSize * recordSize) {
                    file.setLength((long) tableSize * recordSize);
                }
            }
            inicializado = true;
        }
    }
}
