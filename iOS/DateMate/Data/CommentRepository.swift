//
//  WriteRepository.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/14.
//

import Foundation
import FirebaseDatabase
import FirebaseAuth
import Combine

class CommentRepository {
    private let rootRef: DatabaseReference
    
    private let commentRef: DatabaseReference
    
    func getComments(_ key: String) -> AnyPublisher<[Comment], Error> {
        return commentRef.child(key).observer(for: .value).tryMap { result in
            result.children.map { dataSnapshot -> Comment in
                if let snapshot = dataSnapshot as? DataSnapshot, let dic = snapshot.value as? [String: Any] {
                    return Comment(
                        id: dic["uid"] as! String,
                        author: dic["author"] as! String,
                        text: dic["text"] as! String
                    )
                } else {
                    fatalError()
                }
            }
        }.eraseToAnyPublisher()
    }
    
    func addComment(_ key: String, _ text: String) -> AnyPublisher<DatabaseReference, Error> {
        // user 가져오는것도 다시 생각해볼것
        guard let user = Auth.auth().currentUser, let username = user.email?.split(separator: "@").first else {
            fatalError()
        }
        let dic: [String: Any] = [
            "author": username,
            "text": text,
            "uid": user.uid
        ]
        return commentRef.child(key).childByAutoId().setValue(dic).eraseToAnyPublisher()
    }
    
    init() {
        self.rootRef = Database.database().reference()
        self.commentRef = rootRef.child("post-comments")
    }
}
