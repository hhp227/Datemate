//
//  StorageRemoteDataSource.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/26.
//

import Foundation
import FirebaseStorage

class StorageRemoteDataSource {
    let storage: Storage
    
    func uploadFile(fileURL: URL, path: String) async -> Resource<String> {
        do {
            let storageRef = storage.reference().child(path)
            _ = try await storageRef.putFileAsync(from: fileURL)
            let url = try await storageRef.downloadURL()
            return .success(data: url.absoluteString)
        } catch {
            return .error(message: "파일 업로드 실패: \(error.localizedDescription)")
        }
    }
    
    private init(_ storage: Storage) {
        self.storage = storage
    }
    
    private static var instance: StorageRemoteDataSource? = nil
    
    static func getInstance(storage: Storage) -> StorageRemoteDataSource {
        if let instance = self.instance {
            return instance
        } else {
            let storageDataSource = StorageRemoteDataSource(storage)
            self.instance = storageDataSource
            return storageDataSource
        }
    }
}
