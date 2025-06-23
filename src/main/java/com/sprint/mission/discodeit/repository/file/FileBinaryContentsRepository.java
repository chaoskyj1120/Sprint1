package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContents;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FileBinaryContentsRepository implements BinaryContentsRepository, Serializable {

    private static final String BINARY_CONTENTS_FILE_PATH = "./data/binaryContents.ser";

    @Override
    public List<BinaryContents> loadBinaryContents() {
        File file = new File(BINARY_CONTENTS_FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>(); // 비어 있으면 빈 리스트 반환
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<BinaryContents>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveBinaryContents(List<BinaryContents> contentsList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BINARY_CONTENTS_FILE_PATH))) {
            oos.writeObject(contentsList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createBinaryContents(BinaryContents contents) {
        List<BinaryContents> contentsList = loadBinaryContents();
        contentsList.add(contents);
        saveBinaryContents(contentsList);
    }

    @Override
    public BinaryContents getBinaryContentsById(UUID binaryContentsId){
        List<BinaryContents> contentsList = loadBinaryContents();
        return contentsList.stream()
                .filter(content -> content.equalsId(binaryContentsId))
                .findFirst()
                .orElse(null); // 없으면 null 반환
    }

    @Override
    public void delete(UUID profileId){
        List<BinaryContents> contentsList = loadBinaryContents();
        contentsList.removeIf(content -> content.equalsId(profileId));
        saveBinaryContents(contentsList);
    }
}
