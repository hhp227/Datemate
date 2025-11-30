//
//  PhoneAuthView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/30.
//

import SwiftUI

struct PhoneAuthView: View {
    var onVerified: () -> Void
    
    var body: some View {
        Text(/*@START_MENU_TOKEN@*/"Hello, World!"/*@END_MENU_TOKEN@*/)
    }
}

struct PhoneAuthView_Previews: PreviewProvider {
    static var previews: some View {
        PhoneAuthView(onVerified: {})
    }
}
