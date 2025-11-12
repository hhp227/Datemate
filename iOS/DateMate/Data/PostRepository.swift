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
    private let rootRef: DatabaseReference
    
    private let postRef: DatabaseReference
    
    private let userPostRef: DatabaseReference

    func getPost(_ key: String) -> AnyPublisher<Post, Error> {
        return postRef.child(key).observeSingleEvent(of: .value).tryMap { result in
            if let dic = result.value as? [String: Any] {
                return Post(
                    id: dic["uid"] as! String,
                    author: dic["author"] as! String,
                    title: dic["title"] as! String,
                    body: dic["body"] as! String,
                    starCount: dic["starCount"] as! Int,
                    stars: [:],
                    key: result.key
                )
            } else {
                fatalError()
            }
        }.eraseToAnyPublisher()
    }
    
    func addPost(_ title: String, _ content: String) -> AnyPublisher<DatabaseReference, Error> {
        guard let user = Auth.auth().currentUser, let key = postRef.childByAutoId().key else {
            fatalError()
        }
        let postValue: [String: Any] = [
            "uid": user.uid,
            "author": user.email?.split(separator: "@").first ?? "author",
            "title": title,
            "body": content,
            "starCount": 0,
            "stars": [:]
        ]
        let childUpdates: [String: Any] = [
            "/posts/\(key)": postValue,
            "/user-posts/\(user.uid)/\(key)": postValue
        ]
        return rootRef.updateChildValues(childUpdates).eraseToAnyPublisher()
    }
    
    func removePost(_ key: String) -> AnyPublisher<DatabaseReference, Error> {
        guard let user = Auth.auth().currentUser else { fatalError() }
        let childUpdates: [String: Any?] = [
            "/posts/\(key)": nil,
            "/user-posts/\(user.uid)/\(key)": nil
        ]
        return rootRef.updateChildValues(childUpdates).eraseToAnyPublisher()
    }
    
    func getUserPostKeys(_ key: String) -> AnyPublisher<[String], Error> {
        guard let user = Auth.auth().currentUser else { fatalError() }
        return userPostRef.child(user.uid).observer(for: .value).tryMap { dataSnapshot in
            dataSnapshot.children.map { ($0 as! DataSnapshot).key }
        }.eraseToAnyPublisher()
    }
    
    init() {
        self.rootRef = Database.database().reference()
        self.postRef = rootRef.child("posts")
        self.userPostRef = rootRef.child("user-posts")
    }
}
