//
//  PostRemoteDataSource.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/21.
//

import Foundation
import FirebaseFirestore
import Combine

class PostRemoteDataSource {
    let firestore: Firestore
    
    func fetchUserPosts(userId: String) -> AnyPublisher<[Post], Never> {
        let mockPosts = [
            Post(id: "1", userId: userId,
                 title: "Traditional spare ribs baked",
                 content: "Chef John",
                 imageUrls: ["https://picsum.photos/400/200?random=1"], likeCount: 0),
            Post(id: "2", userId: userId,
                 title: "Spice roasted chicken with flavored rice",
                 content: "Mark Kelvin",
                 imageUrls: ["https://picsum.photos/400/200?random=2"], likeCount: 0),
            Post(id: "3", userId: userId,
                 title: "Spicy fried rice with bacon",
                 content: "Chef Anna",
                 imageUrls: ["https://picsum.photos/400/200?random=3"], likeCount: 0),
            Post(id: "4", userId: userId,
                 title: "Classic Beef Wellington",
                 content: "Gordon",
                 imageUrls: ["https://picsum.photos/400/200?random=4"], likeCount: 0)
            ]
        return Just(mockPosts)
            .delay(for: .milliseconds(500), scheduler: RunLoop.main)
            .eraseToAnyPublisher()
    }
    
    private init(_ firestore: Firestore) {
        self.firestore = firestore
    }
    
    private static var instance: PostRemoteDataSource? = nil
    
    static func getInstance(firestore: Firestore) -> PostRemoteDataSource {
        if let instance = self.instance {
            return instance
        } else {
            let postDataSource = PostRemoteDataSource(firestore)
            self.instance = postDataSource
            return postDataSource
        }
    }
}
