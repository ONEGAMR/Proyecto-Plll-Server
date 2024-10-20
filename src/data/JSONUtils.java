package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JSONUtils<T> {

    private String filePath;
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // Constructor con ruta del archivo
    public JSONUtils(String route) {
        this.filePath = route;
    }
    
    // Constructor por defecto
    public JSONUtils() {
        this.filePath = "";
    }

    // Establece la ruta del archivo
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // Guarda un elemento en el archivo JSON
    @SuppressWarnings("unchecked")
    public void saveElement(T t) throws IOException {
        List<T> temp = getElements((Class<T>) t.getClass());
        temp.add(t);
        mapper.writeValue(new File(filePath), temp);
    }

    // Obtiene una lista de elementos desde el archivo JSON
    public List<T> getElements(Class<T> elementClass) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, elementClass));
    }

    // Actualiza el archivo JSON con la lista proporcionada
    public void updateJson(List<T> list) {
        try {
            mapper.writeValue(new File(filePath), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
