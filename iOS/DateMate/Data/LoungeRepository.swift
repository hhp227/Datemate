//
//  LoungeRepository.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/09.
//

import Foundation
import FirebaseDatabase
import Combine

class LoungeRepository {
    private let rootRef: DatabaseReference
    
    private var postRef: DatabaseReference
    
    func getPosts() -> AnyPublisher<[Post], Error> {
        return postRef.observer(for: .value).tryMap { result in
            result.children.map { dataSnapshot -> Post in
                if let snapshot = dataSnapshot as? DataSnapshot, let dic = snapshot.value as? [String: Any] {
                    let post = Post.init(
                        id: dic["uid"] as! String,
                        author: dic["author"] as! String,
                        title: dic["title"] as! String,
                        body: dic["body"] as! String,
                        starCount: dic["starCount"] as! Int,
                        stars: [:],
                        key: snapshot.key
                    )
                    return post
                } else {
                    fatalError()
                }
            }
        }.eraseToAnyPublisher()
    }
    
    func test() -> String {
        return "헬로우"
    }
    
    init() {
        self.rootRef = Database.database().reference()
        self.postRef = rootRef.child("posts")
    }
}

