//
//  StorageRepository.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/26.
//

import Foundation

class StorageRepository {
    private let storageRemoteDataSource: StorageRemoteDataSource
    
    func uploadProfileImage(fileURL: URL, userId: String, index: Int) async -> Resource<String> {
        let timestamp = Int(Date().timeIntervalSince1970 * 1000)
        let path = "users/\(userId)/gallery_\(index)_\(timestamp).jpg"
        return await storageRemoteDataSource.uploadFile(fileURL: fileURL, path: path)
    }
    
    private init(_ storageRemoteDataSource: StorageRemoteDataSource) {
        self.storageRemoteDataSource = storageRemoteDataSource
    }
    
    private static var instance: StorageRepository? = nil
    
    static func getInstance(storageRemoteDataSource: StorageRemoteDataSource) -> StorageRepository {
        if let instance = self.instance {
            return instance
        } else {
            let storageRepository = StorageRepository(storageRemoteDataSource)
            self.instance = storageRepository
            return storageRepository
        }
    }
}
