//
//  Post.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/11.
//

import Foundation
import FirebaseFirestore

struct Post: Identifiable {
    var id: String
    
    var userId: String
    
    var title: String
    
    var content: String
    
    var imageUrls: [String]
    
    var likeCount: Int
    
    var createdAt: Timestamp?
}
