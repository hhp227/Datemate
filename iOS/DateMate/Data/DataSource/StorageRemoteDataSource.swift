//
//  StorageRemoteDataSource.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/26.
//

import Combine
import Foundation
import FirebaseStorage

class StorageRemoteDataSource {
    let storage: Storage
    
    func uploadFile(fileUrl: URL, path: String) -> AnyPublisher<String, Error> {
        Future { promise in
            let storageRef = self.storage.reference().child(path)
            let _ = storageRef.putFile(from: fileUrl, metadata: nil) { _, error in
                if let error = error {
                    promise(.failure(error))
                    return
                }
                storageRef.downloadURL { url, error in
                    if let url = url {
                        promise(.success(url.absoluteString))
                    } else if let error = error {
                        promise(.failure(error))
                    } else {
                        promise(.failure(NSError(domain: "StorageError", code: -1)))
                    }
                }
            }
        }
        .eraseToAnyPublisher()
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
