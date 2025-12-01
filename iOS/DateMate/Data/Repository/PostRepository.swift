//
//  PostDetailRepository.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/14.
//

import Foundation
import FirebaseDatabase
import Combine
import FirebaseAuth

class PostRepository {
    private let postRemoteDataSource: PostRemoteDataSource
    
    func getPosts(_ key: String) -> AnyPublisher<[Post], Error> {
        return Future { promise in
            promise(.success([Post(id: "", userId: "", title: "", content: "", imageUrls: [], likeCount: 0)]))
        }
        .eraseToAnyPublisher()
    }
    
    func fetchUserPosts(userId: String) -> AnyPublisher<Resource<[Post]>, Never> {
        return postRemoteDataSource.fetchUserPosts(userId: userId).asResource()
    }
    
    private init(_ postRemoteDataSource: PostRemoteDataSource) {
        self.postRemoteDataSource = postRemoteDataSource
    }
    
    private static var instance: PostRepository? = nil
    
    static func getInstance(postRemoteDataSource: PostRemoteDataSource) -> PostRepository {
        if let instance = self.instance {
            return instance
        } else {
            let postRepository = PostRepository(postRemoteDataSource)
            self.instance = postRepository
            return postRepository
        }
    }
}
