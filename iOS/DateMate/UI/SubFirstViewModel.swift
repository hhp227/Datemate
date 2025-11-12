//
//  SubFirstViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/13.
//

import Foundation
import SwiftUI

class SubFirstViewModel: ObservableObject {
    @Published var data: String
    
    init(data: String) {
        self.data = data
    }
}
