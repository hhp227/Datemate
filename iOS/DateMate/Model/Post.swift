//
//  Post.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/11.
//

import Foundation

struct Post: Identifiable {
    var id: String
    
    var author: String
    
    var title: String
    
    var body: String
    
    var starCount: Int
    
    var stars: [String: Bool]
    
    var key: String
}
