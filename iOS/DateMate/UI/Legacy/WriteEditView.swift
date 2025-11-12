//
//  WriteEditView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/13.
//

import SwiftUI

struct WriteEditView: View {
    @EnvironmentObject var viewModel: WriteEditViewModel
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var body: some View {
        List {
            ZStack {
                TextEditor(text: $viewModel.title).autocapitalization(.none).keyboardType(.default).disableAutocorrection(true)
            }.listRowInsets(EdgeInsets()).shadow(radius: 1)
            ZStack {
                TextEditor(text: $viewModel.content).autocapitalization(.none).keyboardType(.default).disableAutocorrection(true)
                Text(viewModel.content).opacity(0).padding(.all, 8)
            }.listRowInsets(EdgeInsets()).shadow(radius: 1)
            /*VStack {
                TextField("Enter title", text: $viewModel.title)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
            }.listRowInsets(EdgeInsets()).shadow(radius: 1)
            TextEditor(text: $viewModel.content)
                .frame(height: 180)
                .overlay(RoundedRectangle(cornerRadius: 4)
                            .stroke(Color("TextColor").opacity(0.2), lineWidth: 1)).listRowInsets(EdgeInsets()).shadow(radius: 1)*/
        }.navigationBarTitleDisplayMode(.inline).navigationBarItems(trailing: Button(action: viewModel.actionSend) { Text("Send") }).onReceive(viewModel.$state) { state in
            if state.success {
                presentationMode.wrappedValue.dismiss()
            }
        }
    }
}

struct WriteView_Previews: PreviewProvider {
    static var previews: some View {
        WriteEditView()
    }
}
