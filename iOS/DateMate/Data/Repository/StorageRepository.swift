//
//  StorageRepository.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/26.
//

import Foundation
import Combine

class StorageRepository {
    private let storageRemoteDataSource: StorageRemoteDataSource
    
    private let defaultConcurrency: Int
    
    func uploadProfileImagePublisher(fileUrl: URL, userId: String, index: Int) -> AnyPublisher<Resource<String>, Never> {
        let path = "users/\(userId)/gallery_\(index)_\(Int(Date().timeIntervalSince1970 * 1000)).jpg"
        return storageRemoteDataSource.uploadFile(fileUrl: fileUrl, path: path).asResource()
    }
    
    func uploadAllImages(
        imageUrls: [URL],
        userId: String,
        concurrency: Int? = nil,
        retryCount: Int = 0
    ) -> AnyPublisher<[String], Error> {
        let concurrency = concurrency ?? defaultConcurrency
        let publishers = imageUrls.enumerated().map { (index, url) -> AnyPublisher<String, Error> in
            let path = "users/\(userId)/gallery_\(index)_\(Int(Date().timeIntervalSince1970 * 1000)).jpg"
            return storageRemoteDataSource.uploadFile(fileUrl: url, path: path)
                .retry(retryCount)
                .eraseToAnyPublisher()
        }
        return Publishers.MergeMany(publishers)
            .collect()
            .eraseToAnyPublisher()
    }
    
    private init(_ storageRemoteDataSource: StorageRemoteDataSource, defaultConcurrency: Int = 4) {
        self.storageRemoteDataSource = storageRemoteDataSource
        self.defaultConcurrency = defaultConcurrency
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
