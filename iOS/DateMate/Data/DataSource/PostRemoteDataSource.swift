//
//  PostRemoteDataSource.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/21.
//

import Foundation
import FirebaseFirestore

class PostRemoteDataSource {
    let firestore: Firestore
    
    init(_ firestore: Firestore) {
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
